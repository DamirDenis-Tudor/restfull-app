package org.pos.study.presentation.controllers.professor

import api.academia.Auth
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.dto.professor.ProfessorCreate
import org.pos.study.business.dto.professor.ProfessorUpdate
import org.pos.study.business.interfaces.professor.IProfessorService
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.aspects.InjectEmail
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/professors")
class ProfessorController(
    private val professorService: IProfessorService,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Get all professors",
        description = "Retrieves a paginated list of all professors.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of professors",
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
                description = "Returned when any parameter does not match the expected range.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter is not expected type.",
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
    fun getAllProfessors(
        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()
    ): ResponseEntity<CollectionModel<EntityModel<Professor>>> =
        professorService.getAllProfessors(page, size).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toCollectionModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN, Auth.Role.PROFESSOR])
    @Operation(
        summary = "Get a specific professor by ID",
        description = "Fetches details of a specific professor identified by ID.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Professor details retrieved successfully",
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
                description = "Returned when any parameter does not match the expected range.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter is not expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String
    ): ResponseEntity<EntityModel<*>> {
        email.takeIf(String::isNotBlank)?.let {
            professorService.verifyProfessor(id, email).getOrThrow()
        }

        return professorService.getProfessorById(id).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @Operation(
        summary = "Get the current logged-in professor",
        description = "Fetches the details of the professor who is currently logged in, based on their email.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Current professor details retrieved successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authorization header missing or invalid",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @GetMapping("/me")
    fun getCurrentProfessor(
        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String
    ): ResponseEntity<EntityModel<Professor>> {
        return professorService.getProfessorByEmail(email).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Create a new professor",
        description = "Creates a new professor in the system.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Professor created successfully",
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
                responseCode = "409",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Returned when any parameter does not match the expected range.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Bad request: Invalid data provided",
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
    fun createProfessor(
        @Valid @RequestBody professorCreate: ProfessorCreate
    ): ResponseEntity<EntityModel<Professor>> =
        professorService.createProfessor(professorCreate).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(professorModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Update a professor's information",
        description = "Updates an existing professor's details.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Professor updated successfully",
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
                responseCode = "409",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Returned when any parameter does not match the expected range.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter is not expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PatchMapping("/{id}")
    fun patchProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @Valid @RequestBody professorUpdates: ProfessorUpdate
    ): ResponseEntity<EntityModel<*>> {
        return professorService.updateProfessor(id, professorUpdates).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Delete a professor",
        description = "Deletes a professor from the system based on their ID.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Professor deleted successfully",

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
                description = "Returned when any parameter does not match the expected range.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when parameter is not expected type.",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long
    ): ResponseEntity<Void> =
        professorService.deleteProfessor(id).getOrThrow()
            .let { ResponseEntity.noContent().build() }
}
