package org.pos.study.presentation.controllers.lecture

import api.academia.Auth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.presentation.assemblers.LectureModelAssembler
import org.pos.study.persistence.entities.Lecture
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.presentation.annotations.RequiresRoles
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/lectures/search")
class LectureSearchController(
    private val lectureRepository: LectureRepository,
    private val lectureModelAssembler: LectureModelAssembler
) {

    @Operation(
        summary = "Search for lectures",
        description = "Search for lectures based on various parameters such as name, year, type, etc.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of lectures retrieved successfully",
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
                responseCode = "422",
                description = "Unprocessable entity (invalid parameters)",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Service Unavailable",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No lectures found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @RequiresRoles(roles = [Auth.Role.PROFESSOR])
    @GetMapping
    fun searchLectures(
        @RequestParam(required = false)
        @Size(min = LectureConstraints.LectureName.MIN_SIZE, max = LectureConstraints.LectureName.MAX_SIZE)
        lectureName: String? = null,

        @RequestParam(required = false)
        @Min(value = LectureConstraints.StudyYear.MIN_VALUE)
        @Max(value = LectureConstraints.StudyYear.MAX_VALUE)
        studyYear: Int? = null,

        @RequestParam(required = false)
        lectureType: Lecture.LectureType? = null,

        @RequestParam(required = false)
        categoryType: Lecture.CategoryType? = null,

        @RequestParam(required = false)
        examinationType: Lecture.ExaminationType? = null,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Lecture>>> {

        val lecturesPage = lectureRepository.findAllByCriteria(
            lectureName,
            studyYear,
            lectureType,
            categoryType,
            examinationType,
            PageRequest.of(page, size)
        )

        if (lecturesPage.hasContent())
            return ResponseEntity.ok(lectureModelAssembler.toCollectionModel(lecturesPage))

        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No lecture collection based on the parameters $lectureName, $studyYear, $lectureType, $categoryType, $examinationType found."
        )
    }
}
