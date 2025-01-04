package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.presentation.assemblers.ProfessorModelAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lectures/{lectureId}/professors")
class LectureProfessorController(
    private val lectureProfessorService: ILectureProfessorService,
    private val professorModelAssembler: ProfessorModelAssembler,
    private val lectureModelAssembler: LectureModelAssembler
) {
    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    fun getProfessorByLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<Professor>> =
        lectureProfessorService.getProfessorByLecture(lectureId).getOrThrow()
            .let { ResponseEntity.ok(professorModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.UNKNOWN])
    @PatchMapping("/{professorId}")
    fun updateProfessorForLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable professorId: Long
    ): ResponseEntity<EntityModel<*>> =
        lectureProfessorService.updateProfessorForLecture(lectureId, professorId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
}
