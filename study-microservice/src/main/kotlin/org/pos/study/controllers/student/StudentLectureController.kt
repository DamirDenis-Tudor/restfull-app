package org.pos.study.controllers.student

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.dto.constraints.LectureConstraints
import org.pos.study.dto.constraints.PageConstraints
import org.pos.study.dto.constraints.StudentConstraints
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.StudentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/students/{studentId}/lectures")
class StudentLectureController(
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository,
    private val lectureModelAssembler: LectureModelAssembler,
    private val studentModelAssembler: StudentModelAssembler
) {

    @GetMapping
    fun getLecturesByStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int,

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val student = studentRepository.findById(studentId).orElse(null)

        return student?.let {
            val lectures = lectureRepository.findByStudentsContaining(it, PageRequest.of(page, size))

            if (lectures.hasContent())
                return@let ResponseEntity.ok(
                    lectureModelAssembler.toCollectionModel(
                        page = lectures,
                        studentId = studentId
                    )
                )

            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID $studentId has no lectures.")

        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID $studentId not found.")
    }

    @GetMapping("/{lectureId}")
    fun getLectureByStudent(

        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String,

        @PathVariable
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        studentId: Long

    ): ResponseEntity<EntityModel<Lecture>> {
        val student = studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID $studentId not found.")

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")

        if (lecture.students.any { it.id == student.id })
            return ResponseEntity.ok(lectureModelAssembler.toModel(lecture))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId is not associated with student ID $studentId.")
    }

    @PostMapping("/{lectureId}")
    fun enrollStudentInLecture(

        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String

    ): ResponseEntity<EntityModel<Lecture>> {
        val student = studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID $studentId not found.")

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")

        if (!student.lectures.contains(lecture)) {
            student.lectures.add(lecture)
            lecture.students.add(student)
            studentRepository.save(student)
            lectureRepository.save(lecture)

            return ResponseEntity.ok(lectureModelAssembler.toModel(lecture))
        }
        throw ResponseStatusException(
            HttpStatus.CONFLICT,
            "Student with ID $studentId is already enrolled in lecture with ID $lectureId."
        )
    }

    @DeleteMapping("/{lectureId}")
    fun unrollStudentFromLecture(

        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String

    ): ResponseEntity<EntityModel<*>> {
        val student = studentRepository.findById(studentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID $studentId not found.")

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture with ID $lectureId not found.")

        if (student.lectures.contains(lecture)) {
            student.lectures.remove(lecture)
            lecture.students.remove(student)

            studentRepository.save(student)
            lectureRepository.save(lecture)

            return ResponseEntity.ok(studentModelAssembler.toModel(student))
        }

        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Student with ID $studentId is not enrolled in lecture with ID $lectureId."
        )
    }
}
