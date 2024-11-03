package org.pos.study.controllers.lecture

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.dto.constraints.LectureConstraints
import org.pos.study.dto.constraints.PageConstraints
import org.pos.study.dto.lecture.LectureCreate
import org.pos.study.dto.lecture.LectureUpdate
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.ProfessorRepository
import org.pos.study.repositories.StudentRepository
import org.springframework.data.domain.PageRequest
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
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository
) {
    @GetMapping
    fun getLectures(

        @Min(PageConstraints.Page.MIN_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}") page: Int,

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}") size: Int

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val lecturePage = lectureRepository.findAll(PageRequest.of(page, size))
        if (lecturePage.hasContent())
            return ResponseEntity.ok(lectureModelAssembler.toCollectionModel(lecturePage))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lectures at page $page with size $size not found.")
    }

    @GetMapping("/{lectureId}")
    fun getLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE.toInt(),
            max = LectureConstraints.Id.MAX_SIZE.toInt()
        )
        @PathVariable lectureId: String

    ): ResponseEntity<EntityModel<Lecture>> {
        val lecture = lectureRepository.findById(lectureId)

        if (lecture.isPresent)
            return ResponseEntity.ok(lectureModelAssembler.toModel(lecture.get()))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")
    }

    @PutMapping
    fun createLecture(

        @Valid @RequestBody lecture: LectureCreate

    ): ResponseEntity<EntityModel<*>> {
        val professor = lecture.professorId.let { professorRepository.findById(it).getOrNull() }
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Professor with ID ${lecture.professorId} not found."
            )

        if (lectureRepository.findById(lecture.id).isPresent)
            throw ResponseStatusException(HttpStatus.CONFLICT, "Lecture with ID ${lecture.id} already exists.")

        val newLecture = Lecture(
            id = lecture.id,
            lectureName = lecture.lectureName,
            studyYear = lecture.studyYear,
            lectureType = lecture.lectureType,
            categoryType = lecture.categoryType,
            examinationType = lecture.examinationType,
            professor = professor,
        )

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(lectureModelAssembler.toModel(lectureRepository.save(newLecture)))
    }

    @PatchMapping("/{lectureId}")
    fun patchLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String,

        @Valid @RequestBody
        lectureUpdates: LectureUpdate

    ): ResponseEntity<EntityModel<Lecture>> {
        val existingLecture = lectureRepository.findById(lectureId).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")
        }

        val professor = lectureUpdates.professorId?.let {
            professorRepository.findById(it).getOrNull() ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Professor with ID ${lectureUpdates.professorId} not found."
            )
        }
        val updatedLecture = existingLecture.copy(
            lectureName = lectureUpdates.lectureName?.takeIf { it.isNotBlank() } ?: existingLecture.lectureName,
            studyYear = lectureUpdates.studyYear?.takeIf { it != 0 } ?: existingLecture.studyYear,
            lectureType = lectureUpdates.lectureType ?: existingLecture.lectureType,
            categoryType = lectureUpdates.categoryType ?: existingLecture.categoryType,
            examinationType = lectureUpdates.examinationType ?: existingLecture.examinationType,
            professor = professor ?: existingLecture.professor,
        )

        return ResponseEntity.ok(lectureModelAssembler.toModel(lectureRepository.save(updatedLecture)))
    }

    @DeleteMapping("/{lectureId}")
    fun deleteLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String

    ): ResponseEntity<Any> {
        val lecture = lectureRepository.findById(lectureId)

        if (!lecture.isPresent)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")

        for (student in lecture.get().students) {
            student.lectures.remove(lecture.get())
            studentRepository.save(student)
        }
        lectureRepository.deleteById(lectureId)

        return ResponseEntity.noContent().build()
    }
}
