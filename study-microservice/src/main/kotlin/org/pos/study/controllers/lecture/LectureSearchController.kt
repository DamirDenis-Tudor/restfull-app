package org.pos.study.controllers.lecture

import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.repositories.LectureRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lectures/search")
class LectureSearchController(
    private val lectureRepository: LectureRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun searchLectures(
        @RequestParam(required = false) lectureName: String? = null,
        @RequestParam(required = false) studyYear: Int? = null,
        @RequestParam(required = false) lectureType: Lecture.LectureType? = null,
        @RequestParam(required = false) categoryType: Lecture.CategoryType? = null,
        @RequestParam(required = false) examinationType: Lecture.ExaminationType? = null,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "10") size: Int = 10
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val pageable: Pageable = PageRequest.of(page, size)

        val lecturesPage = lectureRepository.findAllByCriteria(
            lectureName,
            studyYear,
            lectureType,
            categoryType,
            examinationType,
            pageable
        )

        return ResponseEntity.ok(lectureModelAssembler.toCollectionModel(lecturesPage))
    }
}
