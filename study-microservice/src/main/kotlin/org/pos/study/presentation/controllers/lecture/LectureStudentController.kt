package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.business.interfaces.lecture.ILectureStudentService
import org.pos.study.persistence.entities.Student
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
@RequestMapping("/lectures/{lectureId}/students")
class LectureStudentController(
    private val lectureStudentService: ILectureStudentService,
    private val lectureProfessorService: ILectureProfessorService,
    private val studentModelAssembler: StudentModelAssembler,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    @Operation(
        summary = "Get students by lecture",
        description = "Fetch all students enrolled in a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of students retrieved successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Page range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Service Unavailable",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No students found for the given lecture",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getStudentsByLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt(),

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId.toString()).getOrThrow()
        }

        return lectureStudentService.getStudentsByLecture(lectureId, page, size).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toCollectionModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/enroll")
    @Operation(
        summary = "Enroll students in lecture",
        description = "Enroll students in the specified lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Students enrolled successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No students found for the given lecture, or not lecture found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "409",
                description = "One student might be already enrolled.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Page range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Service Unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun enrollStudentsInLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @RequestBody @NotEmpty @Valid
        studentIds: List<@Min(StudentConstraints.Id.MIN_SIZE) @Max(StudentConstraints.Id.MAX_SIZE) Long>,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<EntityModel<*>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId.toString()).getOrThrow()
        }

        return lectureStudentService.enrollStudentsInLecture(lectureId, studentIds).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/unenroll")
    @Operation(
        summary = "Unenroll students from lecture",
        description = "Unenroll students from the specified lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Students unenrolled successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Page range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Service Unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun unenrollStudentsInLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @RequestBody @NotEmpty @Valid
        studentIds: List<@Min(StudentConstraints.Id.MIN_SIZE) @Max(StudentConstraints.Id.MAX_SIZE) Long>,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<EntityModel<*>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId.toString()).getOrThrow()
        }

        return lectureStudentService.unenrollStudentsInLecture(lectureId, studentIds).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.STUDENT])
    @Operation(
        summary = "Check if a student is enrolled in a lecture",
        description = "Checks if a student is enrolled in a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "True if the student is enrolled, otherwise false",
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

    @GetMapping("enrolled")
    fun isStudentEnrolledInLecture(
        @PathVariable
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        lectureId: Int,

        @InjectEmail(forRole = Auth.Role.STUDENT)
        email: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(
            lectureStudentService
                .isStudentEnrolledInLecture(email, lectureId.toString())
                .getOrThrow()
        )
    }
}