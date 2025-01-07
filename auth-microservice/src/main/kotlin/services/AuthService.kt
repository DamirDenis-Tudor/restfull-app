package org.pos.services

import api.academia.Auth
import api.academia.AuthServiceGrpcKt

class AuthService(private val tokenService: TokenService) : AuthServiceGrpcKt.AuthServiceCoroutineImplBase() {


    private fun buildErrorResponse(ex: Throwable): Auth.ErrorResponse =
        Auth.ErrorResponse.newBuilder()
            .setException(ex::class.java.simpleName)
            .setMessage(ex.message)
            .build()


    override suspend fun login(request: Auth.LoginRequest): Auth.LoginResponse {
        return tokenService.login(request.username, request.password)
            .fold(
                onSuccess = { (token, role) ->
                    Auth.LoginResponse
                        .newBuilder()
                        .setSuccess(
                            Auth.TokenLoginResponse.newBuilder()
                                .setToken(token)
                                .setMessage("Login successful")
                                .setRole(role.toString())
                                .build()
                        ).build()
                },
                onFailure = { ex ->
                    println(ex.stackTraceToString())

                    Auth.LoginResponse
                        .newBuilder()
                        .setError(buildErrorResponse(ex))
                        .build()
                }
            )
    }

    override suspend fun validateToken(request: Auth.TokenRequest): Auth.TokenValidResponse {
        val decodedJWT = tokenService.validateToken(request.token).getOrElse {
            return Auth.TokenValidResponse.newBuilder().setError(buildErrorResponse(it)).build()
        }

        val role = decodedJWT.getClaim("role").asInt()
            ?: return Auth.TokenValidResponse
                .newBuilder()
                .setError(buildErrorResponse(Exception("Missing role")))
                .build()

        val id = decodedJWT.subject.takeIf { it.isNotBlank() }
            ?: return Auth.TokenValidResponse
                .newBuilder()
                .setError(buildErrorResponse(Exception("Missing id")))
                .build()

        val roleEnum = runCatching {
            Auth.Role.forNumber(role)
        }.getOrElse {
            return Auth.TokenValidResponse
                .newBuilder()
                .setError(buildErrorResponse(Exception("Invalid role value: $role")))
                .build()
        }

        return Auth.TokenValidResponse
            .newBuilder()
            .setSuccess(
                Auth.TokenValid.newBuilder()
                    .setRole(roleEnum)
                    .setId(id)
                    .build()
            ).build()
    }


    override suspend fun invalidateToken(request: Auth.TokenRequest): Auth.InvalidateTokenResponse {
        tokenService.validateToken(request.token).getOrElse {
            return Auth.InvalidateTokenResponse.newBuilder().setError(buildErrorResponse(it)).build()
        }

        return tokenService.invalidateToken(request.token)
            .fold(
                onSuccess = {
                    Auth.InvalidateTokenResponse.newBuilder()
                        .setSuccess("Token invalidated successfully")
                        .build()
                },
                onFailure = { ex ->
                    println(ex.stackTraceToString())

                    Auth.InvalidateTokenResponse
                        .newBuilder()
                        .setError(buildErrorResponse(ex))
                        .build()
                }
            )
    }
}
