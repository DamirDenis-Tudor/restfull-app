package org.pos.study.controllers.professor

import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.ProfessorRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/professors/{professorId}/lectures")
class ProfessorLectureController(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getLecturesByProfessor(
        @PathVariable professorId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val professor = professorRepository.findById(professorId).orElse(null)

        return professor?.let {
            val pageable: Pageable = PageRequest.of(page, size)
            val lecturePage = lectureRepository.findByProfessor(it, pageable)
            ResponseEntity.ok(
                lectureModelAssembler.toCollectionModel(page = lecturePage, professorId = professorId)
            )
        } ?: ResponseEntity.notFound().build()
    }


    @GetMapping("/{lectureId}")
    fun getLectureByProfessor(
        @PathVariable professorId: Long,
        @PathVariable lectureId: Long
    ): ResponseEntity<EntityModel<Lecture>> {
        return professorRepository
            .findById(professorId)
            .orElse(null)
            .let {
                lectureRepository.findById(lectureId)
                    .filter { it.professor.id.toLong() == professorId }
                    .map { lectureModelAssembler.toModel(it) }
                    .map { ResponseEntity.ok(it) }
                    .orElse(ResponseEntity.notFound().build())
            } ?: ResponseEntity.notFound().build()
    }
}