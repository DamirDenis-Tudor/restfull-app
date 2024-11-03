package org.pos.study.controllers.professor

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.dto.constraints.LectureConstraints
import org.pos.study.dto.constraints.PageConstraints
import org.pos.study.dto.constraints.ProfessorConstraints
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.ProfessorRepository
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/professors/{professorId}/lectures")
class ProfessorLectureController(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getLecturesByProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @PathVariable
        professorId: Long,

        @Min(PageConstraints.Page.MIN_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int,

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val professor = professorRepository.findById(professorId)

        if (professor.isPresent) {
            return ResponseEntity.ok(
                lectureModelAssembler.toCollectionModel(
                    page = lectureRepository.findByProfessor(professor.get(), PageRequest.of(page, size)),
                    professorId = professorId
                )
            )
        }

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Professor with ID $professorId not found.")
    }

    @GetMapping("/{lectureId}")
    fun getLectureByProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE) @PathVariable
        professorId: Long,

        @Size(
            min = LectureConstraints.Id.MIN_SIZE,
            max = LectureConstraints.Id.MAX_SIZE
        ) @PathVariable
        lectureId: String

    ): ResponseEntity<EntityModel<Lecture>> {
        val professor = professorRepository.findById(professorId)

        if (professor.isPresent) {
            return lectureRepository.findById(lectureId)
                .filter { it.professor.id.toLong() == professorId }
                .map { lectureModelAssembler.toModel(it) }
                .map { ResponseEntity.ok(it) }
                .orElseThrow {
                    throw ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Professor with ID $professorId has no lecture with ID $lectureId."
                    )
                }
        }

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Professor with ID $professorId not found.")
    }
}
