package org.pos.study.presentation.controllers.student

import api.academia.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.dto.student.StudentCreate
import org.pos.study.business.dto.student.StudentUpdate
import org.pos.study.business.exceptions.EntityUnverifiable
import org.pos.study.business.interfaces.student.IStudentService
import org.pos.study.persistence.entities.Student
import org.pos.study.presentation.annotations.InjectId
import org.pos.study.presentation.annotations.RequiresRoles
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students")
class StudentController(
    private val studentService: IStudentService,
    private val studentModelAssembler: StudentModelAssembler
) {

    @Operation(
        summary = "Get all students",
        description = "Retrieves a paginated list of all students.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of students",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authorization header missing or invalid",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Current user role is not allowed to this endpoint.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Returned when any parameter does not match the expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter is not expected type..",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @GetMapping
    fun getAllStudents(
        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()
    ): ResponseEntity<CollectionModel<EntityModel<Student>>> =
        studentService.getAllStudents(page, size).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toCollectionModel(it)) }

    @Operation(
        summary = "Get a student by ID",
        description = "Retrieves a student by ID, requires the student email to be injected from the JWT token constraints if the user is a student.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Student found",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized, token invalid or missing",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden, user does not have the necessary permissions",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Student not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Invalid ID parameter provided",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @RequiresRoles(roles = [Auth.Role.ADMIN, Auth.Role.STUDENT, Auth.Role.PROFESSOR])
    @GetMapping("/{id}")
    fun getStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @InjectId(forRole = Auth.Role.STUDENT)
        idAuth: String
    ): ResponseEntity<EntityModel<Student>> {
        idAuth.takeIf { it.isNotBlank() }?.let {
            if (idAuth.toLong() != id) {
                throw EntityUnverifiable("Student with id $idAuth cannot view info of student $id ")
            }
        }

        return studentService.getStudentById(id).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }
    }

    @Operation(
        summary = "Create a new student",
        description = "Creates a new student record.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Student created",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid student data",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized, token invalid or missing",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden, user does not have necessary permissions",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @PostMapping
    fun createStudent(
        @Valid @RequestBody studentCreate: StudentCreate
    ): ResponseEntity<EntityModel<Student>> {
        return studentService.createStudent(studentCreate).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(studentModelAssembler.toModel(it)) }
    }

    @Operation(
        summary = "Update an existing student",
        description = "Updates a student's record by ID.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Student updated",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request, invalid data format",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Invalid parameters ranges",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Invalid student data",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Student not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "409",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @PutMapping("/{id}")
    fun updateStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,
        @Valid @RequestBody studentUpdate: StudentUpdate
    ): ResponseEntity<EntityModel<Student>> {
        return studentService.updateStudent(id, studentUpdate).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }
    }

    @Operation(
        summary = "Delete a student",
        description = "Deletes a student record by ID.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Student deleted",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized, token invalid or missing",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden, user does not have necessary permissions",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Student not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Invalid parameters ranges",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Invalid parameter data",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @DeleteMapping("/{id}")
    fun deleteStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        return studentService.deleteStudent(id).getOrThrow()
            .let { ResponseEntity.noContent().build() }
    }
}