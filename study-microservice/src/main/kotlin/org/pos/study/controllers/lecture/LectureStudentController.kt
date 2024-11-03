package org.pos.study.controllers.lecture

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Student
import org.pos.study.dto.constraints.LectureConstraints
import org.pos.study.dto.constraints.PageConstraints
import org.pos.study.dto.constraints.StudentConstraints
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.StudentRepository
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
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int,

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int

    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with id $lectureId not found")

        val studentPage = studentRepository.findByLecturesContaining(lecture, PageRequest.of(page, size))

        if (studentPage.hasContent())
            return ResponseEntity.ok(studentModelAssembler.toCollectionModel(studentPage))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Students for lecture $lectureId not found")
    }

    @PostMapping("/{studentId}")
    fun enrollStudentInLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String,

        @Min(StudentConstraints.Id.MIN_SIZE)
        @PathVariable
        studentId: Long

    ): ResponseEntity<EntityModel<*>> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with id $lectureId not found")

        val student = studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $studentId not found")

        if (student in lecture.students)
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Student with id ${student.id} already enrolled in lecture with id $lectureId"
            )

        lectureRepository.save(lecture.apply { lecture.students.add(student) })

        return ResponseEntity.ok(lectureModelAssembler.toModel(lecture))
    }

    @DeleteMapping("/{studentId}")
    fun removeStudentFromLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: String,

        @Min(StudentConstraints.Id.MIN_SIZE)
        @PathVariable
        studentId: Long

    ): ResponseEntity<Void> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with id $lectureId not found")

        val student = studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $studentId not found")

        if (student !in lecture.students)
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Student with id ${student.id} not enrolled in lecture with id $lectureId."
            )

        lectureRepository.save(lecture.apply { lecture.students.remove(student) }) // Fix: Use remove instead of add

        return ResponseEntity.noContent().build()
    }
}
