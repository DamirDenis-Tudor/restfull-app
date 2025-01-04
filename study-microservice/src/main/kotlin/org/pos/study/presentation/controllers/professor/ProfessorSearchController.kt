package org.pos.study.presentation.controllers.professor

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.pos.study.persistence.entities.Professor
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.persistence.repositories.ProfessorRepository
import org.pos.study.presentation.aspects.RequiresRoles
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/professors/search")
class ProfessorSearchController(
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @GetMapping
    @Operation(
        summary = "Search for professors",
        description = "Searches for professors based on various search criteria.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of professors matching the search criteria",
                content = [Content(mediaType = "application/hal+json")]
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
                responseCode = "416",
                description = "Returned when any parameter does not match the expected range",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Returned when page range is not in expected value range",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No professors found for the given search criteria",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun searchProfessors(

        @Size(min = ProfessorConstraints.FirstName.MIN_SIZE_SEARCH, max = ProfessorConstraints.FirstName.MAX_SIZE)
        @RequestParam(required = false)
        firstName: String? = null,

        @Size(min = ProfessorConstraints.LastName.MIN_SIZE_SEARCH, max = ProfessorConstraints.LastName.MAX_SIZE)
        @RequestParam(required = false)
        lastName: String? = null,

        @Size(min = ProfessorConstraints.Email.MIN_SIZE_SEARCH, max = ProfessorConstraints.Email.MAX_SIZE)
        @RequestParam(required = false)
        email: String? = null,

        @Size(min = ProfessorConstraints.Affiliation.MIN_SIZE_SEARCH, max = ProfessorConstraints.Affiliation.MAX_SIZE)
        @RequestParam(required = false)
        affiliation: String? = null,

        @RequestParam(required = false)
        graderType: Professor.GraderType? = null,

        @RequestParam(required = false)
        associationType: Professor.AssociationType? = null,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Professor>>> {

        val professorsPage = professorRepository.findAllByCriteria(
            firstName,
            lastName,
            email,
            affiliation,
            graderType,
            associationType,
            PageRequest.of(page, size)
        )

        if (professorsPage.hasContent()) {
            return ResponseEntity.ok(professorModelAssembler.toCollectionModel(professorsPage))
        }

        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No professor collection based on the parameters firstName=$firstName, lastName=$lastName, email=$email, affiliation=$affiliation, graderType=$graderType, associationType=$associationType, page=$page, size=$size found."
        )
    }
}
