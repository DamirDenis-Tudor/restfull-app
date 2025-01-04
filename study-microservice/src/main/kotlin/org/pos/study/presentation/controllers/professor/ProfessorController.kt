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
import org.pos.study.persistence.entities.Student
import org.pos.study.presentation.aspects.InjectEmail
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/professors")
class ProfessorController(
    private val professorService: IProfessorService,
    private val professorModelAssembler: ProfessorModelAssembler
) {
    @RequiresRoles(roles = [Auth.Role.ADMIN])
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
    @GetMapping("/me")
    fun getCurrentProfessor(
        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String
    ): ResponseEntity<EntityModel<Professor>> {
        return professorService.getProfessorByEmail(email).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @PostMapping
    fun createProfessor(
        @Valid @RequestBody professorCreate: ProfessorCreate
    ): ResponseEntity<EntityModel<Professor>> =
        professorService.createProfessor(professorCreate).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(professorModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @PatchMapping("/{id}")
    fun patchProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @Valid @RequestBody professorUpdates: ProfessorUpdate

    ): ResponseEntity<EntityModel<*>> =
        professorService.updateProfessor(id, professorUpdates).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @DeleteMapping("/{id}")
    fun deleteProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long

    ): ResponseEntity<Map<String, String>> =
        professorService.deleteProfessor(id).getOrThrow()
            .let { ResponseEntity.noContent().build() }
}
