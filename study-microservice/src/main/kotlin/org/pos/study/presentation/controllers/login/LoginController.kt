package org.pos.study.presentation.controllers.login

import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import com.google.protobuf.method
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.pos.study.business.dto.login.LoginRequest
import org.pos.study.business.dto.login.LoginResponse
import org.pos.study.business.dto.login.LogoutResponse
import org.pos.study.business.interfaces.lecture.ILectureStudentService
import org.pos.study.business.interfaces.professor.IProfessorService
import org.pos.study.business.interfaces.student.IStudentService
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.annotations.RequiresRoles
import org.pos.study.presentation.aspects.CurrentUserContext
import org.pos.study.presentation.assemblers.LoginModelAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/login")
class LoginController(
    private val professorService: IProfessorService,
    private val studentService: IStudentService,
    private val authGrpcStub: AuthServiceGrpcKt.AuthServiceCoroutineStub,
    private val loginModelAssembler: LoginModelAssembler
) {

    @Operation(
        summary = "Perform the login request",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "True if user is logged in successfully",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed to login user",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter request contains invalid data",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Returned when when an internal server error occurred.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<EntityModel<LoginResponse>> = runBlocking {
        val loginRequest = Auth.LoginRequest
            .newBuilder()
            .setUsername(loginRequest.email)
            .setPassword(loginRequest.password)
            .build()

        val response = runCatching { authGrpcStub.login(loginRequest) }.getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Authentication Service is not available.")
        }

        if (response.hasError()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, response.error.message)
        }

        val loginResponse = LoginResponse(
            message = response.success.message,
            token = response.success.token,
            role = Auth.Role.forNumber(response.success.role.toInt())
        )

        val userId = when(Auth.Role.forNumber(response.success.role.toInt())){
                Auth.Role.PROFESSOR -> professorService.getProfessorByEmail(loginRequest.username).getOrThrow().id
                Auth.Role.STUDENT ->  studentService.getStudentByEmail(loginRequest.username).getOrThrow().id
                Auth.Role.ADMIN, Auth.Role.UNKNOWN,  Auth.Role.UNRECOGNIZED-> null
            }

        return@runBlocking ResponseEntity.ok(loginModelAssembler.toModel(loginResponse, userId = userId?.toString()))
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN, Auth.Role.STUDENT, Auth.Role.PROFESSOR])
    @Operation(
        summary = "Perform the logout request",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "True if user is logged out successfully",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Failed to logout user",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Current user role is not allowed to this endpoint.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter request contains invalid data",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Returned when when an internal server error occurred.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )

    @DeleteMapping
    fun logout(): ResponseEntity<EntityModel<LogoutResponse>> = runBlocking {
        val logoutRequest = Auth.TokenRequest
            .newBuilder()
            .setToken(CurrentUserContext.getToken())
            .build()

        val response = runCatching { authGrpcStub.invalidateToken(logoutRequest) }.getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Authentication Service is not available.")
        }

        if (response.hasError()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, response.error.message)
        }

        return@runBlocking ResponseEntity.ok(EntityModel.of(LogoutResponse(message = response.success)))
    }
}