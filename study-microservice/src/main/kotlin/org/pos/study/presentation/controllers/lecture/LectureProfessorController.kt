package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/lectures/{lectureId}/professors")
class LectureProfessorController(
    private val lectureProfessorService: ILectureProfessorService,
    private val professorModelAssembler: ProfessorModelAssembler,
    private val lectureModelAssembler: LectureModelAssembler
) {
    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
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
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<Professor>> =
        lectureProfessorService.getProfessorByLecture(lectureId).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }

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
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable professorId: Long
    ): ResponseEntity<EntityModel<*>> =
        lectureProfessorService.updateProfessorForLecture(lectureId, professorId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
}
