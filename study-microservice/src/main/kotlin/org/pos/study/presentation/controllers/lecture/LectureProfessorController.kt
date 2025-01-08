package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.business.interfaces.lecture.ILectureStudentService
import org.pos.study.business.interfaces.professor.IProfessorLectureService
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.annotations.InjectId
import org.pos.study.presentation.annotations.RequiresRoles
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.pos.study.presentation.assemblers.lecture.LectureProfessorModelAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lectures/{lectureId}/professors")
class LectureProfessorController(
    private val professorLectureService: IProfessorLectureService,
    private val lectureProfessorService: ILectureProfessorService,
    private val lectureStudentService: ILectureStudentService,
    private val professorModelAssembler: ProfessorModelAssembler,
    private val lectureProfessorModelAssembler: LectureProfessorModelAssembler
) {
    @RequiresRoles(roles = [Auth.Role.PROFESSOR, Auth.Role.STUDENT])
    @GetMapping
    @Operation(
        summary = "Get professor by lecture",
        description = "Fetches the professor for the given lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Professor retrieved successfully",
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
    fun getProfessorByLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: Int,

        @InjectId(forRole = Auth.Role.STUDENT)
        id: String
    ): ResponseEntity<EntityModel<Professor>> {
        id.takeIf { it.isNotEmpty() }?.let {
            lectureStudentService.isStudentEnrolledInLecture(id, lectureId.toString()).getOrThrow()
        }

        return lectureProfessorService.getProfessorByLecture(lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }
    }
    @RequiresRoles(roles = [Auth.Role.UNKNOWN])
    @PatchMapping("/{professorId}")
    @Operation(
        summary = "Update professor for lecture",
        description = "Updates the professor assigned to a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Professor updated successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Lecture or professor not found",
                content = [Content(mediaType = "application/json")]
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
    fun updateProfessorForLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: Int,

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable professorId: Long
    ): ResponseEntity<EntityModel<*>> =
        lectureProfessorService.updateProfessorForLecture(lectureId.toString(), professorId).getOrThrow()
            .let { ResponseEntity.ok(lectureProfessorModelAssembler.toModel(it)) }


    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping("/owner")
    @Operation(
        summary = "Check if professor owns the lecture",
        description = "Verifies if the professor is the owner of the specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "True if the professor owns the lecture, false otherwise",
                content = [Content(mediaType = "text/plain")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authorization header missing or invalid",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Current user role is not allowed to this endpoint",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Returned when any parameter does not match the expected range",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter is not expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun isProfessorOwnerOfLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE) @PathVariable
        lectureId: Int,

        @InjectId(forRole = Auth.Role.PROFESSOR)
        id: String

    ): ResponseEntity<Boolean> {
        val isOwner = professorLectureService.isProfessorOwnerOfLecture(id, lectureId.toString()).getOrThrow()
        return ResponseEntity.ok(isOwner)
    }
}
