package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kotlinx.coroutines.runBlocking
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.lecture.LectureCreate
import org.pos.study.business.dto.lecture.LectureUpdate
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.business.interfaces.lecture.ILectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.presentation.annotations.InjectEmail
import org.pos.study.presentation.annotations.RequiresRoles
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
import org.pos.study.business.dto.lecture.LectureRequestBody
import org.pos.study.presentation.annotations.InjectAuthorizationHeader
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

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
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable lectureId: String
    ): ResponseEntity<EntityModel<Lecture>> =
        lectureService.getLectureById(lectureId.toString()).getOrThrow()
            .let { ResponseEntity.ok(lectureModelAssembler.toModel(it)) }

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
    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PutMapping
    @Transactional
    fun createLecture(
        @Valid @RequestBody lecture: LectureCreate,

        @InjectAuthorizationHeader
        authorizationHeader: String,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String,

        ): ResponseEntity<EntityModel<*>> {

        restTemplate.exchange(
            "http://0.0.0.0:8000/api/academia/lectures",
            HttpMethod.PUT,
            HttpEntity<String>(
                ObjectMapper().writeValueAsString(LectureRequestBody(lecture.id)),
                HttpHeaders().apply {
                    this.set(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    this.set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                },
            ),
            String::class.java
        )

        return lectureService.createLecture(lecture, email).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(lectureModelAssembler.toModel(it)) }
    }


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
    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @PatchMapping("/{lectureId}")
    fun patchLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
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
    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @DeleteMapping("/{lectureId}")
    @Transactional
    fun deleteLecture(
        @Min(LectureConstraints.Id.MIN_SIZE)
        @Max(LectureConstraints.Id.MAX_SIZE)
        @PathVariable
        lectureId: Int,

        @InjectAuthorizationHeader
        authorizationHeader: String,

        @InjectEmail(forRole = Auth.Role.PROFESSOR)
        email: String = "",
    ): ResponseEntity<Void> {
        email.takeIf { it.isNotBlank() }?.let {
            lectureProfessorService.isProfessorOwnerOfLecture(email, lectureId.toString()).getOrThrow()
        }

        lectureService.deleteLecture(lectureId.toString()).getOrThrow()

        restTemplate.exchange(
            "http://0.0.0.0:8000/api/academia/lectures/$lectureId",
            HttpMethod.DELETE,
            HttpEntity<String>(HttpHeaders().apply {
                this.set(HttpHeaders.AUTHORIZATION, authorizationHeader)
            }),
            String::class.java
        )

        return ResponseEntity.noContent().build()
    }
}
