package org.pos.study.controllers.lecture

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.dto.lecture.LectureOptional
import org.pos.study.repositories.LectureRepository
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lectures/search")
class LectureSearchController(
    private val lectureRepository: LectureRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun searchLectures(
        @Valid @RequestParam lectureOptional: LectureOptional,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(30) @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {

        val lecturesPage = lectureRepository.findAllByCriteria(
            lectureOptional.lectureName,
            lectureOptional.studyYear,
            lectureOptional.lectureType,
            lectureOptional.categoryType,
            lectureOptional.examinationType,
            PageRequest.of(page, size)
        )

        return ResponseEntity.ok(lectureModelAssembler.toCollectionModel(lecturesPage))
    }
}
