package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
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
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.business.interfaces.lecture.ILectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.aspects.InjectEmail
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/lectures")
class LectureController(
    private val lectureModelAssembler: LectureModelAssembler,
    private val lectureService: ILectureService,
    private val lectureProfessorService: ILectureProfessorService,
) {
    private val restTemplate = RestTemplate()

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    @Operation(
        summary = "Get list of lectures",
        description = "Returns a paginated list of lectures.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of lectures",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Parameter range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Authorization service is unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
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
    @Operation(
        summary = "Get a specific lecture",
        description = "Returns the details of a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Lecture details",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Lecture not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Parameter range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Authorization service is unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<Lecture>> =
        lectureService.getLectureById(lectureId).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }


    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PutMapping
    @Operation(
        summary = "Create a new lecture",
        description = "Creates a new lecture based on the provided information.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Lecture created successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "409",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Parameter range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Authorization service is unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createLecture(
        @Valid @RequestBody lecture: LectureCreate,
    ): ResponseEntity<EntityModel<*>> {
        return lectureService.createLecture(lecture).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/{lectureId}")
    @Operation(
        summary = "Update lecture details",
        description = "Updates the details of a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Lecture updated successfully",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Lecture not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "409",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Parameter range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Authorization service is unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun patchLecture(
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String,

        @Valid @RequestBody lectureUpdates: LectureUpdate,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String,
    ): ResponseEntity<EntityModel<Lecture>> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId).getOrThrow()
        }

        return lectureService.updateLecture(lectureId, lectureUpdates).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @DeleteMapping("/{lectureId}")
    @Transactional
    @Operation(
        summary = "Delete a specific lecture",
        description = "Deletes a specific lecture.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Lecture deleted successfully",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Lecture not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Parameter range is out of bounds",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Authorization service or Disciplines service is unavailable",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteLecture(
        @PathVariable
        @Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
        lectureId: String,

        @RequestHeader(HttpHeaders.AUTHORIZATION)
        authorizationHeader: String,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String,
    ): ResponseEntity<Void> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId).getOrThrow()
        }

        lectureService.deleteLecture(lectureId).getOrThrow()

        restTemplate.exchange(
            "http://localhost:8000/lectures/$lectureId",
            HttpMethod.DELETE,
            HttpEntity<String>(HttpHeaders().apply {
                this.set(HttpHeaders.AUTHORIZATION, authorizationHeader)
            }),
            String::class.java
        )

        return ResponseEntity.noContent().build()
    }
}
