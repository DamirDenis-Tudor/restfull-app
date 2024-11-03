package org.pos.study.controllers.student

import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.StudentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        @PathVariable studentId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        val student = studentRepository.findById(studentId).orElse(null)

        return student?.let {
            val pageable: Pageable = PageRequest.of(page, size)
            val lecturePage = lectureRepository.findByStudentsContaining(it, pageable)
            ResponseEntity.ok(
                lectureModelAssembler.toCollectionModel(page = lecturePage, studentId = studentId)
            )
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/{lectureId}")
    fun getLectureByStudent(
        @PathVariable studentId: Long,
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<Lecture>> {
        return lectureRepository
            .findById(lectureId).orElse(null)
            ?.takeIf { it.students.any { student -> student.id == studentId } }
            ?.let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/{lectureId}")
    fun enrollStudentInLecture(
        @PathVariable studentId: Long,
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<Lecture>> {
        val student = studentRepository.findById(studentId).orElse(null)
            ?: return ResponseEntity.notFound().build()
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        return when {
            student.lectures.contains(lecture) -> ResponseEntity.status(HttpStatus.CONFLICT).body(null)
            else -> {
                student.lectures.add(lecture)
                lecture.students.add(student)
                studentRepository.save(student)
                lectureRepository.save(lecture)

                ResponseEntity.ok(lectureModelAssembler.toModel(lecture))
            }
        }
    }

    @DeleteMapping("/{lectureId}")
    fun unrollStudentFromLecture(
        @PathVariable studentId: Long,
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<*>> {
        val student = studentRepository.findById(studentId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Student with ID $studentId not found.")))

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Lecture with ID $lectureId not found.")))

        return if (!student.lectures.contains(lecture)) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Student with ID $studentId is not enrolled in lecture with ID $lectureId.")))
        } else {
            student.lectures.remove(lecture)
            lecture.students.remove(student)

            studentRepository.save(student)
            lectureRepository.save(lecture)

            ResponseEntity.ok(studentModelAssembler.toModel(student))
        }
    }
}