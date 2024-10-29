package org.pos.study.controllers.professor

import org.pos.study.controllers.assemblers.ProfessorModelAssembler
import org.pos.study.domain.Professor
import org.pos.study.dto.professor.ProfessorUpdate
import org.pos.study.repositories.ProfessorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/professors")
class ProfessorController(
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @GetMapping
    fun getAllProfessors(
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<CollectionModel<EntityModel<Professor>>> {
        val professorsPage: Page<Professor> = professorRepository.findAll(pageable)
        val professorModels = professorModelAssembler.toCollectionModel(professorsPage)
        return ResponseEntity.ok(professorModels)
    }

    @GetMapping("/{id}")
    fun getProfessor(@PathVariable id: Long): ResponseEntity<EntityModel<*>> {
        val professor = professorRepository.findById(id)
        return if (professor.isPresent) {
            ResponseEntity.ok(professorModelAssembler.toModel(professor.get()))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Professor with ID $id not found.")))
        }
    }

    @PostMapping
    fun createProfessor(@RequestBody professor: Professor): ResponseEntity<EntityModel<Professor>> {
        val savedProfessor = professorRepository.save(professor)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(professorModelAssembler.toModel(savedProfessor))
    }

    @PatchMapping("/{id}")
    fun patchProfessor(
        @PathVariable id: Long,
        @RequestBody professorUpdates: ProfessorUpdate
    ): ResponseEntity<EntityModel<*>> {
        val existingProfessor = professorRepository.findById(id)

        return if (existingProfessor.isPresent) {
            val currentProfessor = existingProfessor.get()
            val updatedProfessor = currentProfessor.copy(
                firstName = professorUpdates.firstName ?: currentProfessor.firstName,
                lastName = professorUpdates.lastName ?: currentProfessor.lastName,
                email = professorUpdates.email ?: currentProfessor.email,
                affiliation = professorUpdates.affiliation ?: currentProfessor.affiliation,
                graderType = professorUpdates.graderType ?: currentProfessor.graderType,
                associationType = professorUpdates.associationType ?: currentProfessor.associationType
            )

            ResponseEntity.ok(professorModelAssembler.toModel(professorRepository.save(updatedProfessor)))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Professor with ID $id not found.")))
        }
    }


    @DeleteMapping("/{id}")
    fun deleteProfessor(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return if (professorRepository.existsById(id)) {
            professorRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Professor with ID $id not found."))
        }
    }
}
