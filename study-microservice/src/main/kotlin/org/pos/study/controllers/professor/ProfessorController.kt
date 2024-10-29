package org.pos.study.controllers

import org.pos.study.controllers.assemblers.ProfessorModelAssembler
import org.pos.study.domain.Professor
import org.pos.study.repositories.ProfessorRepository
import org.springframework.hateoas.EntityModel
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/academia/professors")
class ProfessorController(
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @GetMapping("/{id}")
    fun getProfessor(@PathVariable id: Long): EntityModel<Professor>? =
        professorRepository.findById(id).get().let { professorModelAssembler.toModel(it) }

    @PostMapping
    fun createProfessor(@RequestBody professor: Professor): EntityModel<Professor> {
        return professorModelAssembler.toModel(professorRepository.save(professor))
    }

    @PutMapping("/{id}")
    fun updateProfessor(@PathVariable id: Long, @RequestBody professor: Professor): EntityModel<Professor>? {
        val updatedProfessor = professor.copy(id = professorRepository.findById(id).get().id)
        return professorModelAssembler.toModel(professorRepository.save(updatedProfessor))
    }

    @DeleteMapping("/{id}")
    fun deleteProfessor(@PathVariable id: Long) {
        professorRepository.deleteById(id)
    }
}
