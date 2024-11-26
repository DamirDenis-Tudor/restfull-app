package org.pos.study.presentation.controllers.student

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.interfaces.student.IStudentLectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students/{studentId}/lectures")
class StudentLectureController(
    private val studentLectureService: IStudentLectureService,
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
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> =
        studentLectureService.getLecturesByStudent(studentId, page, size).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toCollectionModel(page = it, studentId = studentId)) }


    @GetMapping("/{lectureId}")
    fun getLectureByStudent(
        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String,

        @PathVariable
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        studentId: Long

    ): ResponseEntity<EntityModel<Lecture>> =
        studentLectureService.getLectureByStudent(studentId, lectureId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }


    @PostMapping("/{lectureId}")
    fun enrollStudentInLecture(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String

    ): ResponseEntity<EntityModel<Lecture>> =
        studentLectureService.enrollStudentInLecture(studentId, lectureId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }


    @DeleteMapping("/{lectureId}")
    fun unrollStudentFromLecture(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable studentId: Long,

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String

    ): ResponseEntity<EntityModel<*>> =
        studentLectureService.unrollStudentFromLecture(studentId, lectureId).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }

    @GetMapping("/{lectureId}/enrollment")
    fun isStudentEnrolledInLecture(
        @PathVariable
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        studentId: Long,

        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String

    ): ResponseEntity<Boolean> =
        ResponseEntity.ok(
            studentLectureService
                .isStudentEnrolledInLecture(studentId, lectureId)
                .getOrThrow()
        )

}
