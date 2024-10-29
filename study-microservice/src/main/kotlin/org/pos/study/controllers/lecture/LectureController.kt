package org.pos.study.controllers

import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.repositories.LectureRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/academia/lectures")
class LectureController(
    private val lectureRepository: LectureRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getLectures(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val lecturePage = lectureRepository.findAll(pageable)

        return if (lecturePage.hasContent()) {
            ResponseEntity.ok(lectureModelAssembler.toCollectionModel(lecturePage))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}")
    fun getLecture(@PathVariable id: Long): ResponseEntity<EntityModel<Lecture>> {
        val lecture = lectureRepository.findById(id)
        return if (lecture.isPresent) {
            ResponseEntity.ok(lectureModelAssembler.toModel(lecture.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createLecture(@RequestBody lecture: Lecture): ResponseEntity<EntityModel<Lecture>> {
        val savedLecture = lectureRepository.save(lecture)
        return ResponseEntity.status(HttpStatus.CREATED).body(lectureModelAssembler.toModel(savedLecture))
    }

    @PutMapping("/{id}")
    fun updateLecture(@PathVariable id: Long, @RequestBody lecture: Lecture): ResponseEntity<EntityModel<Lecture>> {
        val existingLecture = lectureRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found")
        }
        val updatedLecture = lecture.copy(id = existingLecture.id)
        return ResponseEntity.ok(lectureModelAssembler.toModel(lectureRepository.save(updatedLecture)))
    }

    @DeleteMapping("/{id}")
    fun deleteLecture(@PathVariable id: Long): ResponseEntity<Void> {
        if (!lectureRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found")
        }
        lectureRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
