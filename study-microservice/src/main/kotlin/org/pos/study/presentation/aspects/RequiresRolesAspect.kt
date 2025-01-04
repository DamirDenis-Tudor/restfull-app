package org.pos.study.presentation.aspects

import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
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

    @Around("@annotation(requiresRoles)")
    fun checkRole(joinPoint: ProceedingJoinPoint, requiresRoles: RequiresRoles): Any? = runBlocking {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing.")

        if (!authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid token format.")
        }

        val token = authHeader.split(" ")[1]

        val validateRequest = Auth.TokenRequest.newBuilder()
            .setToken(token)
            .build()

        val validateResponse = authGrpcStub.validateToken(validateRequest)

        if (validateResponse.hasError()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, validateResponse.error.message)
        }
        println(joinPoint.args.forEach { println(it) })

        if (!requiresRoles.roles.contains(validateResponse.success.role)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for your role.")
        }

        joinPoint.`this`
    }
}