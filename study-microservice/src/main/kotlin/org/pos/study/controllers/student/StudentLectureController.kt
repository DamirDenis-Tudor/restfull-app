package org.pos.study.controllers

import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.domain.Lecture
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.StudentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/academia/students/{studentId}/lectures")
class StudentLectureController(
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @GetMapping
    fun getLecturesByProfessor(
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
        @PathVariable lectureId: Long
    ): ResponseEntity<EntityModel<Lecture>> {
        return lectureRepository
            .findById(lectureId).orElse(null)
            ?.takeIf { it.students.any { student -> student.id == studentId } }
            ?.let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
            ?: ResponseEntity.notFound().build()
    }

}