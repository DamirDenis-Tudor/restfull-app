package org.pos.study.controllers.lecture

import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Student
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.StudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.swing.text.html.parser.Entity

@RestController
@RequestMapping("/lectures/{lectureId}/students")
class LectureStudentController(
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @GetMapping
    fun getStudentsByLecture(
        @PathVariable lectureId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        val pageable: Pageable = PageRequest.of(page, size)

        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val studentPage = studentRepository.findByLecturesContaining(lecture, pageable)

        return if (studentPage.hasContent()) {
            ResponseEntity.ok(studentModelAssembler.toCollectionModel(studentPage))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{studentId}")
    fun enrollStudentInLecture(
        @PathVariable lectureId: Long,
        @PathVariable studentId: Long
    ): ResponseEntity<Void> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val student = studentRepository.findById(studentId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        if (student in lecture.students) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        lecture.students.add(student)
        lectureRepository.save(lecture)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{studentId}")
    fun removeStudentFromLecture(
        @PathVariable lectureId: Long,
        @PathVariable studentId: Long
    ): ResponseEntity<Void> {
        val lecture = lectureRepository.findById(lectureId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val student = studentRepository.findById(studentId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        if (student !in lecture.students) {
            return ResponseEntity.notFound().build()
        }

        lecture.students.remove(student)
        lectureRepository.save(lecture)

        return ResponseEntity.noContent().build()
    }
}
