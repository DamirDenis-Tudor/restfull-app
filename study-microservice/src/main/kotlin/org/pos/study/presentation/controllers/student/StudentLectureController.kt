package org.pos.study.presentation.controllers.student

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.interfaces.student.IStudentLectureService
import org.pos.study.business.interfaces.student.IStudentService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.annotations.InjectEmail
import org.pos.study.presentation.annotations.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content

@RestController
@RequestMapping("/students/{studentId}/lectures")
class StudentLectureController(
    private val studentService: IStudentService,
    private val studentLectureService: IStudentLectureService,
    private val lectureModelAssembler: LectureModelAssembler,
    private val studentModelAssembler: StudentModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.STUDENT, Auth.Role.PROFESSOR])
    @Operation(
        summary = "Get all lectures for a student",
        description = "Retrieves a paginated list of lectures for the specified student.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of lectures",
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
                responseCode = "416",
                description = "Returned when any parameter does not match the expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter range is not in expected value range.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @GetMapping
    fun getLecturesByStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt(),

        @InjectEmail(forRole = Auth.Role.STUDENT)
        email: String
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        email.takeIf { it.isNotBlank() }?.let {
            studentService.verifyStudent(studentId, it).getOrThrow()
        }

        return studentLectureService.getLecturesByStudent(studentId, page, size).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toCollectionModel(page = it, studentId = studentId)) }
    }

    @RequiresRoles(roles = [Auth.Role.STUDENT])
    @Operation(
        summary = "Get a specific lecture for a student",
        description = "Retrieves a specific lecture for a student by their lecture ID.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Lecture details",
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
                responseCode = "416",
                description = "Returned when any parameter does not match the expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @GetMapping("/{lectureId}")
    fun getLectureByStudent(
        @PathVariable
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        lectureId: Int,

        @PathVariable
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        studentId: Long,

        @InjectEmail(forRole = Auth.Role.STUDENT)
        email: String
    ): ResponseEntity<EntityModel<Lecture>> {
        email.takeIf { it.isNotBlank() }?.let {
            studentService.verifyStudent(studentId, it).getOrThrow()
        }

        return studentLectureService.getLectureByStudent(studentId, lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Enroll a student in a lecture",
        description = "Enrolls a student in a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Enrollment success",
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
                description = "Lecture or Student not found.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "409",
                description = "If student is already enrolled.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/{lectureId}")
    fun enrollStudentInLecture(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @PathVariable
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        lectureId: Int
    ): ResponseEntity<EntityModel<Lecture>> {
        return studentLectureService.enrollStudentInLecture(studentId, lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Unroll a student from a lecture",
        description = "Unenrolls a student from a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Unenrollment success",
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
                description = "Lecture or Student not found.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @DeleteMapping("/{lectureId}")
    fun unrollStudentFromLecture(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<*>> {
        return studentLectureService.unrollStudentFromLecture(studentId, lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }
    }

}
