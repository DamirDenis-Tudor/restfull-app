package org.pos.study.presentation.controllers.professor

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.interfaces.professor.IProfessorLectureService
import org.pos.study.business.interfaces.professor.IProfessorService
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
import org.pos.study.presentation.assemblers.lecture.LectureProfessorModelAssembler
import org.pos.study.presentation.controllers.lecture.LectureProfessorController

@RestController
@RequestMapping("/professors/{id}/lectures")
class ProfessorLectureController(
    private val professorService: IProfessorService,
    private val professorLectureService: IProfessorLectureService,
    private val lecturesProfessorModelAssembler: LectureProfessorModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    @Operation(
        summary = "Get lectures by professor",
        description = "Retrieves a paginated list of lectures for a specific professor.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of lectures for the professor",
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
                description = "Current user role is not allowed to this endpoint",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
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
                description = "Returned when the authorization service is not available",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getLecturesByProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt(),

        @InjectId(forRole = Auth.Role.PROFESSOR)
        idAuth: String

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        if(id != idAuth.toLong()) {
            //throw EntityUnverifiable("Current user $id is not allowed to this endpoint for user with id $idAuth ")
        }

        return professorLectureService.getLecturesByProfessor(id, page, size).getOrThrow()
            .let { ResponseEntity.ok(lecturesProfessorModelAssembler.toCollectionModel(page = it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping("/{lectureId}")
    @Operation(
        summary = "Get specific lecture by professor",
        description = "Fetches a specific lecture assigned to the professor.",
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
                description = "Current user role is not allowed to this endpoint",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
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
                description = "Returned when the authorization service is not available",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getLectureByProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE) @PathVariable
        id: Long,

        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE) @PathVariable
        lectureId: Int,

        @InjectId(forRole = Auth.Role.PROFESSOR)
        idAuth: String

    ): ResponseEntity<EntityModel<Lecture>> {
        if(id != idAuth.toLong()) {
            throw EntityUnverifiable("Current user $id is not allowed to this endpoint for user with id $idAuth ")
        }

        return professorLectureService.getLectureByProfessor(id, lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(lecturesProfessorModelAssembler.toModel(it)) }
    }

}
