package org.pos.study.controllers.lecture

import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.dto.lecture.LectureCreate
import org.pos.study.dto.lecture.LectureUpdate
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.ProfessorRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/lectures")
class LectureController(
    private val lectureRepository: LectureRepository,
    private val lectureModelAssembler: LectureModelAssembler,
    private val professorRepository: ProfessorRepository
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

    @GetMapping("/{lectureId}")
    fun getLecture(@PathVariable lectureId: Long): ResponseEntity<EntityModel<Lecture>> {
        val lecture = lectureRepository.findById(lectureId)
        return if (lecture.isPresent) {
            ResponseEntity.ok(lectureModelAssembler.toModel(lecture.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createLecture(@RequestBody lecture: LectureCreate): ResponseEntity<EntityModel<*>> {
        val professor = lecture.professorId.let { professorRepository.findById(it).getOrNull()}
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Professor with ID ${lecture.professorId} not found.")))

        val newLecture = Lecture(
            lectureName = lecture.lectureName,
            studyYear = lecture.studyYear,
            lectureType = lecture.lectureType,
            categoryType = lecture.categoryType,
            examinationType = lecture.examinationType,
            professor = professor,
        )

        return runCatching {
            lectureRepository.save(newLecture)
        }.fold(
            onSuccess = { savedLecture ->
                ResponseEntity.status(HttpStatus.CREATED).body(lectureModelAssembler.toModel(savedLecture))
            },
            onFailure = { exception ->
                when (exception) {
                    is DataIntegrityViolationException -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(EntityModel.of(mapOf("message" to exception.message)))

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(EntityModel.of(mapOf("message" to exception.message)))
                }
            }
        )
    }


    @PatchMapping("/{lectureId}")
    fun patchLecture(
        @PathVariable lectureId: Long,
        @RequestBody lectureUpdates: LectureUpdate
    ): ResponseEntity<EntityModel<Lecture>> {
        val existingLecture = lectureRepository.findById(lectureId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found")
        }

        val updatedLecture = existingLecture.copy(
            lectureName = lectureUpdates.lectureName?.takeIf { it.isNotBlank() } ?: existingLecture.lectureName,
            studyYear = lectureUpdates.studyYear?.takeIf { it != 0 } ?: existingLecture.studyYear,
            lectureType = lectureUpdates.lectureType ?: existingLecture.lectureType,
            categoryType = lectureUpdates.categoryType ?: existingLecture.categoryType,
            examinationType = lectureUpdates.examinationType ?: existingLecture.examinationType,
        )

        return ResponseEntity.ok(lectureModelAssembler.toModel(lectureRepository.save(updatedLecture)))
    }

    @DeleteMapping("/{lectureId}")
    fun deleteLecture(@PathVariable lectureId: Long): ResponseEntity<Void> {
        if (!lectureRepository.existsById(lectureId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found")
        }
        lectureRepository.deleteById(lectureId)
        return ResponseEntity.noContent().build()
    }
}
