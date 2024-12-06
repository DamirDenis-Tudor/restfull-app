package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import kotlinx.coroutines.runBlocking
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.lecture.LectureCreate
import org.pos.study.business.dto.lecture.LectureUpdate
import org.pos.study.business.interfaces.lecture.ILectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/lectures")
class LectureController(
    private val lectureModelAssembler: LectureModelAssembler,
    private val lectureService: ILectureService,
) {
    private val restTemplate = RestTemplate()

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    fun getLectures(

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> = runBlocking {
        lectureService.getLectures(page, size).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toCollectionModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping("/{lectureId}")
    fun getLecture(

        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String

    ): ResponseEntity<EntityModel<Lecture>> =
        lectureService.getLectureById(lectureId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }


    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PutMapping
    fun createLecture(

        @Valid @RequestBody lecture: LectureCreate

    ): ResponseEntity<EntityModel<*>> =
        lectureService.createLecture(lecture).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(lectureModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/{lectureId}")
    fun patchLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @Valid @RequestBody lectureUpdates: LectureUpdate

    ): ResponseEntity<EntityModel<Lecture>> =
        lectureService.updateLecture(lectureId, lectureUpdates).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }


    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @DeleteMapping("/{lectureId}")
    @Transactional
    fun deleteLecture(

        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String,

        @RequestHeader(HttpHeaders.AUTHORIZATION)
        authorizationHeader: String?

    ): ResponseEntity<Void> {
        lectureService.deleteLecture(lectureId).getOrThrow()

        restTemplate.exchange(
            "http://localhost:8000/lectures/$lectureId",
            HttpMethod.DELETE,
            HttpEntity<String>(HttpHeaders().apply {
                this.set(HttpHeaders.AUTHORIZATION, authorizationHeader!!)
            }),
            String::class.java
        )

        return ResponseEntity.noContent().build()
    }

}
