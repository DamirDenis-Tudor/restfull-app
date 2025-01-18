package org.pos.study.presentation.controllers.student

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.interfaces.student.IStudentLectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.annotations.RequiresRoles
import org.pos.study.presentation.assemblers.lecture.LectureModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import org.pos.study.business.exceptions.EntityUnverifiable
import org.pos.study.presentation.annotations.InjectId
import org.pos.study.presentation.assemblers.lecture.LectureStudentModelAssembler

@RestController
@RequestMapping("/students/{studentId}/lectures")
class StudentLectureController(
    private val studentLectureService: IStudentLectureService,
    private val lectureModelAssembler: LectureModelAssembler,
    private val lectureStudentModelAssembler: LectureStudentModelAssembler
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
                responseCode = "400",
                content = [Content(mediaType = "application/json")]
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

        @InjectId(forRole = Auth.Role.STUDENT)
        idAuth: String
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        idAuth.takeIf { it.isNotBlank() }?.let {
            if (idAuth.toLong() != studentId) {
                throw EntityUnverifiable("Student with id $idAuth cannot view info of student $studentId ")
            }
        }

        return studentLectureService.getLecturesByStudent(studentId, page, size).getOrThrow()
            .let { ResponseEntity.ok(lectureStudentModelAssembler.toCollectionModel(page = it)) }
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
                responseCode = "400",
                content = [Content(mediaType = "application/json")]
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

        @InjectId(forRole = Auth.Role.STUDENT)
        idAuth: String
    ): ResponseEntity<EntityModel<Lecture>> {
        idAuth.takeIf { it.isNotBlank() }?.let {
            if (idAuth.toLong() != studentId) {
                throw EntityUnverifiable("Student with id $idAuth cannot view info of student $studentId ")
            }
        }

        return studentLectureService.getLectureByStudent(studentId, lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

}
