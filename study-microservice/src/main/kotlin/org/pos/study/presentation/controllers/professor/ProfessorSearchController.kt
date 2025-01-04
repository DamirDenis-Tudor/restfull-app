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

@RestController
@RequestMapping("/professors/search")
class ProfessorSearchController(
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @GetMapping
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
