package org.pos.study.presentation.aspects

import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.pos.study.presentation.annotations.InjectAuthorizationHeader
import org.pos.study.presentation.annotations.InjectEmail
import org.pos.study.presentation.annotations.RequiresRoles
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Aspect
@Component
class RequiresRolesAspect(
    private val authGrpcStub: AuthServiceGrpcKt.AuthServiceCoroutineStub,
    private val request: HttpServletRequest
) {
    private val logger = LoggerFactory.getLogger(RequiresRolesAspect::class.java)

    @Around("@annotation(requiresRoles)")
    fun checkRole(joinPoint: ProceedingJoinPoint, requiresRoles: RequiresRoles): Any? = runBlocking {
        logger.info("checkRole() called with: {}", joinPoint.signature.name)

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing.")

        if (!authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid token format.")
        }

        val validateRequest = Auth.TokenRequest.newBuilder()
            .setToken(authHeader.split(" ")[1])
            .build()

        val validateResponse = runCatching { authGrpcStub.validateToken(validateRequest) }.getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Authentication Service is not available.")
        }

        logger.info("Authorization header contains the following: role=${validateResponse.success.role}, email=${validateResponse.success.id}")

        if (validateResponse.hasError()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, validateResponse.error.message)
        }

        if (!requiresRoles.roles.contains(validateResponse.success.role)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for your role.")
        }

        (joinPoint.signature as MethodSignature).method.parameters.withIndex().forEach { (index, parameter) ->
            parameter.getAnnotation(InjectEmail::class.java)?.let { injectValue ->
                joinPoint.args[index] = if (injectValue.forRole == validateResponse.success.role) {
                    validateResponse.success.id
                } else ""
            }
            parameter.getAnnotation(InjectAuthorizationHeader::class.java)?.let { injectValue ->
                joinPoint.args[index] = authHeader
            }
        }

        joinPoint.proceed(joinPoint.args)
    }
}