package org.pos.study.controllers.lecture

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.controllers.assemblers.ProfessorModelAssembler
import org.pos.study.domain.Professor
import org.pos.study.dto.constraints.LectureConstraints
import org.pos.study.dto.constraints.ProfessorConstraints
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.ProfessorRepository
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/lectures/{lectureId}/professors")
class LectureProfessorController(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository,
    private val professorModelAssembler: ProfessorModelAssembler,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getProfessorByLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String,

    ): ResponseEntity<EntityModel<Professor>> {
        val lecture = lectureRepository.findById(lectureId)

        if (lecture.isPresent)
            lecture.get().professor?.let{ return ResponseEntity.ok(professorModelAssembler.toModel(it)) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId has no professor.")

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")
    }

    @PatchMapping("/{professorId}")
    fun updateProfessorForLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String,

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable
        professorId: Long

    ): ResponseEntity<EntityModel<*>> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")

        val professor = professorRepository.findById(professorId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Professor with ID $professorId not found.")

        lectureRepository.save(lecture.apply { this.professor = professor })

        return ResponseEntity.ok(lectureModelAssembler.toModel(lecture))
    }
}
