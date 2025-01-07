package org.pos.study.presentation.controllers.login

import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import com.google.protobuf.method
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.pos.study.business.dto.login.LoginRequest
import org.pos.study.business.dto.login.LoginResponse
import org.pos.study.business.interfaces.lecture.ILectureStudentService
import org.pos.study.business.interfaces.professor.IProfessorService
import org.pos.study.business.interfaces.student.IStudentService
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.assemblers.LoginModelAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
}