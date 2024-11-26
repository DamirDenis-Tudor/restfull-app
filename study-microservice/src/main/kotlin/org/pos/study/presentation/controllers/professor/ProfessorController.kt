package org.pos.study.presentation.controllers.professor

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.pos.study.persistence.entities.Professor
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.dto.professor.ProfessorCreate
import org.pos.study.business.dto.professor.ProfessorUpdate
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.ProfessorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/professors")
class ProfessorController(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

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

    ): ResponseEntity<CollectionModel<EntityModel<Professor>>> {
        val professorsPage: Page<Professor> = professorRepository.findAll(PageRequest.of(page, size))

        if (professorsPage.hasContent())
            return ResponseEntity.ok(professorModelAssembler.toCollectionModel(professorsPage))

        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No professor collection with size $size and page $page found."
        )
    }

    @GetMapping("/{id}")
    fun getProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long

    ): ResponseEntity<EntityModel<*>> {
        val professor = professorRepository.findById(id)

        if (professor.isPresent)
            return ResponseEntity.ok(professorModelAssembler.toModel(professor.get()))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "No professor with id $id found.")
    }

    @PostMapping
    fun createProfessor(

        @Valid @RequestBody professorCreate: ProfessorCreate

    ): ResponseEntity<EntityModel<Professor>> {

        val professor = Professor(
            firstName = professorCreate.firstName,
            lastName = professorCreate.lastName,
            email = professorCreate.email,
            affiliation = professorCreate.affiliation,
            associationType = professorCreate.associationType,
            graderType = professorCreate.graderType,
        )

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(professorModelAssembler.toModel(professorRepository.save(professor)))
    }

    @PatchMapping("/{id}")
    fun patchProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @Valid @RequestBody
        professorUpdates: ProfessorUpdate

    ): ResponseEntity<EntityModel<*>> {
        val existingProfessor = professorRepository.findById(id)

        if (existingProfessor.isPresent) {
            val currentProfessor = existingProfessor.get()

            val updatedProfessor = currentProfessor.copy(
                firstName = professorUpdates.firstName ?: currentProfessor.firstName,
                lastName = professorUpdates.lastName ?: currentProfessor.lastName,
                email = professorUpdates.email ?: currentProfessor.email,
                affiliation = professorUpdates.affiliation ?: currentProfessor.affiliation,
                graderType = professorUpdates.graderType ?: currentProfessor.graderType,
                associationType = professorUpdates.associationType ?: currentProfessor.associationType
            )

            return ResponseEntity.ok(professorModelAssembler.toModel(professorRepository.save(updatedProfessor)))
        }

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Professor with id $id does not exist.")
    }

    @DeleteMapping("/{id}")
    fun deleteProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long

    ): ResponseEntity<Map<String, String>> {
        if (professorRepository.existsById(id)) {
            lectureRepository.setProfessorToNull(id)
            professorRepository.deleteById(id)
            return ResponseEntity.noContent().build()
        }

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Professor with id $id does not exist.")
    }
}
