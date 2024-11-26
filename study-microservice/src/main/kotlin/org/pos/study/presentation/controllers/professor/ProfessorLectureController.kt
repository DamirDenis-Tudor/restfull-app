package org.pos.study.presentation.controllers.professor

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.interfaces.professor.IProfessorLectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/professors/{id}/lectures")
class ProfessorLectureController(
    private val professorLectureService: IProfessorLectureService,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getLecturesByProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> =
        professorLectureService.getLecturesByProfessor(id, page, size).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toCollectionModel(page = it, professorId = id)) }

    @GetMapping("/{lectureId}")
    fun getLectureByProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE) @PathVariable
        id: Long,

        @Size(
            min = LectureConstraints.Id.MIN_SIZE,
            max = LectureConstraints.Id.MAX_SIZE
        ) @PathVariable
        lectureId: String

    ): ResponseEntity<EntityModel<Lecture>> =
        professorLectureService.getLectureByProfessor(id, lectureId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }

    @GetMapping("/{lectureId}/ownership")
    fun isProfessorOwnerOfLecture(
        @Min(ProfessorConstraints.Id.MIN_SIZE) @PathVariable id: Long,

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE) @PathVariable
        lectureId: String

    ): ResponseEntity<Boolean> {
        val isOwner = professorLectureService.isProfessorOwnerOfLecture(id, lectureId).getOrThrow()
        return ResponseEntity.ok(isOwner)
    }

}
