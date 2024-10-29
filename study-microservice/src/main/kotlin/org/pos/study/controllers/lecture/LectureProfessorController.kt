package org.pos.study.controllers.lecture

import org.pos.study.controllers.assemblers.ProfessorModelAssembler
import org.pos.study.domain.Professor
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.ProfessorRepository
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lectures/{lectureId}/professor")
class LectureProfessorController(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler
) {

    @GetMapping
    fun getProfessorByLecture(
        @PathVariable lectureId: Long,
    ): ResponseEntity<EntityModel<Professor>> {
        return lectureRepository.findById(lectureId)
            .map { lecture ->
                lecture.professor.let { professor ->
                    ResponseEntity.ok(professorModelAssembler.toModel(professor))
                }
            }.orElse(ResponseEntity.notFound().build())
    }

    @PatchMapping("/{professorId}")
    fun updateProfessorForLecture(
        @PathVariable lectureId: Long,
        @PathVariable professorId: Long
    ): ResponseEntity<Void> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val professor = professorRepository.findById(professorId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        lecture.professor = professor
        lectureRepository.save(lecture)

        return ResponseEntity.ok().build()
    }

}