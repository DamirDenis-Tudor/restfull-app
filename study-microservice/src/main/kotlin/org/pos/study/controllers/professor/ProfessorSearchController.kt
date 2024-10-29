package org.pos.study.controllers.professor

import org.pos.study.controllers.assemblers.ProfessorModelAssembler
import org.pos.study.domain.Professor
import org.pos.study.repositories.ProfessorRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/professors/search")
class ProfessorSearchController(
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @GetMapping
    fun searchProfessors(
        @RequestParam(required = false) firstName: String? = null,
        @RequestParam(required = false) lastName: String? = null,
        @RequestParam(required = false) email: String? = null,
        @RequestParam(required = false) affiliation: String? = null,
        @RequestParam(required = false) graderType: Professor.GraderType? = null,
        @RequestParam(required = false) associationType: Professor.AssociationType? = null,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "10") size: Int = 10
    ): ResponseEntity<CollectionModel<EntityModel<Professor>>> {
        val pageable: Pageable = PageRequest.of(page, size)

        val professorsPage = professorRepository.findAllByCriteria(
            firstName,
            lastName,
            email,
            affiliation,
            graderType,
            associationType,
            pageable
        )

        return ResponseEntity.ok(professorModelAssembler.toCollectionModel(professorsPage))
    }
}