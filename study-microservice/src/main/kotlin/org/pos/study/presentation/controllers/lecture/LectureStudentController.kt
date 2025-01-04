package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.business.interfaces.lecture.ILectureStudentService
import org.pos.study.persistence.entities.Student
import org.pos.study.presentation.aspects.InjectEmail
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lectures/{lectureId}/students")
class LectureStudentController(
    private val lectureStudentService: ILectureStudentService,
    private val lectureProfessorService: ILectureProfessorService,
    private val studentModelAssembler: StudentModelAssembler,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    fun getStudentsByLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt(),

        @InjectEmail(forRole = Auth.Role.ADMIN)
        email: String

    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId).getOrThrow()
        }

        return lectureStudentService.getStudentsByLecture(lectureId, page, size).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toCollectionModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/enroll")
    fun enrollStudentsInLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @RequestBody @NotEmpty @Valid
        studentIds: List<@Min(StudentConstraints.Id.MIN_SIZE) @Max(StudentConstraints.Id.MAX_SIZE) Long>,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<EntityModel<*>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId).getOrThrow()
        }

        return lectureStudentService.enrollStudentsInLecture(lectureId, studentIds).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/unenroll")
    fun unenrollStudentsInLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @RequestBody @NotEmpty @Valid
        studentIds: List<@Min(StudentConstraints.Id.MIN_SIZE) @Max(StudentConstraints.Id.MAX_SIZE) Long>,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<EntityModel<*>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId).getOrThrow()
        }

        return lectureStudentService.unenrollStudentsInLecture(lectureId, studentIds).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }
}