package org.pos.study.presentation.controllers.professor

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.business.interfaces.professor.IProfessorLectureService
import org.pos.study.business.interfaces.professor.IProfessorService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.aspects.InjectEmail
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/professors/{id}/lectures")
class ProfessorLectureController(
    private val professorService: IProfessorService,
    private val professorLectureService: IProfessorLectureService,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    fun getLecturesByProfessor(

        @Min(ProfessorConstraints.Id.MIN_SIZE)
        @Max(ProfessorConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt(),

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {
        email.takeIf(String::isNotBlank)?.let{
            professorService.verifyProfessor(id, email).getOrThrow()
        }

        return professorLectureService.getLecturesByProfessor(id, page, size).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toCollectionModel(page = it, professorId = id)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping("/{lectureId}")
    fun getLectureByProfessor(
        @Min(ProfessorConstraints.Id.MIN_SIZE) @PathVariable
        id: Long,

        @Size(
            min = LectureConstraints.Id.MIN_SIZE,
            max = LectureConstraints.Id.MAX_SIZE
        ) @PathVariable
        lectureId: String,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<EntityModel<Lecture>> {
        email.takeIf(String::isNotBlank)?.let{
            professorService.verifyProfessor(id, email).getOrThrow()
        }

        return professorLectureService.getLectureByProfessor(id, lectureId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping("/{lectureId}/ownership")
    fun isProfessorOwnerOfLecture(
        @Min(ProfessorConstraints.Id.MIN_SIZE) @PathVariable id: Long,

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE) @PathVariable
        lectureId: String,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String

    ): ResponseEntity<Boolean> {
        email.takeIf(String::isNotBlank)?.let{
            professorService.verifyProfessor(id, email).getOrThrow()
        }

        val isOwner = professorLectureService.isProfessorOwnerOfLecture(id, lectureId).getOrThrow()
        return ResponseEntity.ok(isOwner)
    }

}
