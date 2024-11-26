package org.pos.study.presentation.controllers.lecture

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.pos.study.persistence.entities.Student
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.StudentRepository

import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/lectures/{lectureId}/students")
class LectureStudentController(
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getStudentsByLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with id $lectureId not found")

        val studentPage = studentRepository.findByLecturesContaining(lecture, PageRequest.of(page, size))

        if (studentPage.hasContent())
            return ResponseEntity.ok(studentModelAssembler.toCollectionModel(studentPage))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Students for lecture $lectureId not found")
    }

    @PatchMapping("/enroll")
    fun enrollStudentsInLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @RequestBody @NotEmpty @Valid
        studentIds: List<@Min(StudentConstraints.Id.MIN_SIZE) @Max(StudentConstraints.Id.MAX_SIZE) Long>

    ): ResponseEntity<EntityModel<*>> {

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with id $lectureId not found")

        val students = studentIds.map {
            studentId -> studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $studentId not found")
        }

        students.forEach { student ->
            if (student in lecture.students)
                throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student with id ${student.id} already enrolled in lecture with id $lectureId"
                )
        }

        students.forEach { student ->
            student.lectures.add(lecture)
            lecture.students.add(student)

            studentRepository.save(student)
            lectureRepository.save(lecture)
        }

        return ResponseEntity.ok(lectureModelAssembler.toModel(lecture))
    }

    @PatchMapping("/unenroll")
    fun unenrollStudentsInLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @RequestBody @NotEmpty @Valid
        studentIds: List<@Min(StudentConstraints.Id.MIN_SIZE) @Max(StudentConstraints.Id.MAX_SIZE) Long>

    ): ResponseEntity<EntityModel<*>> {

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with id $lectureId not found")

        val students = studentIds.map {
                studentId -> studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $studentId not found")
        }

        students.forEach { student ->
            if (student !in lecture.students)
                throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student with id ${student.id} is not enrolled in lecture with id $lectureId"
                )
        }

        students.forEach { student ->
            student.lectures.remove(lecture)
            lecture.students.remove(student)

            studentRepository.save(student)
            lectureRepository.save(lecture)
        }

        return ResponseEntity.ok(lectureModelAssembler.toModel(lecture))
    }
}
