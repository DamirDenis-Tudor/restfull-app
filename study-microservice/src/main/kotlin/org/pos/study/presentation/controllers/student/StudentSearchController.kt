package org.pos.study.presentation.controllers.student

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.presentation.assemblers.student.StudentModelAssembler
import org.pos.study.persistence.entities.Student
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.persistence.repositories.StudentRepository
import org.pos.study.presentation.annotations.RequiresRoles
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import org.pos.study.business.exceptions.EntityNotFound

@RestController
@RequestMapping("/students/search")
class StudentSearchController(
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @Operation(
        summary = "Search for students based on various criteria",
        description = "Searches for students using first name, last name, email, cycle type, study year, or student group. Supports pagination.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "A paginated list of students matching the search criteria",
                content = [Content(mediaType = "application/hal+json")]
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authorization header missing or invalid",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden: Current user does not have required roles",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "416",
                description = "Parameters out of acceptable range",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Parameters out of acceptable types",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "503",
                description = "Returned when the authorization service is not available.",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @GetMapping
    fun searchStudents(

        @Size(min = StudentConstraints.FirstName.MIN_SIZE, max = StudentConstraints.FirstName.MAX_SIZE)
        @Parameter(description = "First name of the student to search for", example = "John")
        @RequestParam(required = false) firstName: String? = null,

        @Size(min = StudentConstraints.LastName.MIN_SIZE, max = StudentConstraints.LastName.MAX_SIZE)
        @Parameter(description = "Last name of the student to search for", example = "Doe")
        @RequestParam(required = false) lastName: String? = null,

        @Size(min = StudentConstraints.Email.MIN_SIZE, max = StudentConstraints.Email.MAX_SIZE)
        @Parameter(description = "Email address of the student to search for", example = "john.doe@example.com")
        @RequestParam(required = false) email: String? = null,

        @Parameter(description = "Cycle type of the student to search for")
        @RequestParam(required = false) cycleType: Student.CycleType? = null,

        @Min(StudentConstraints.StudyYear.MIN_VALUE)
        @Max(StudentConstraints.StudyYear.MAX_VALUE)
        @Parameter(description = "Study year of the student", example = "2")
        @RequestParam(required = false) studyYear: Int? = null,

        @Min(StudentConstraints.StudentGroup.MIN_VALUE)
        @Max(StudentConstraints.StudentGroup.MAX_VALUE)
        @Parameter(description = "Student group", example = "1")
        @RequestParam(required = false) studentGroup: Int? = null,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @Parameter(description = "Page number", example = "0")
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @Parameter(description = "Number of students per page", example = "10")
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        val pageable: Pageable = PageRequest.of(page, size)

        val studentsPage = studentRepository.findAllByCriteria(
            firstName,
            lastName,
            email,
            cycleType,
            studyYear,
            studentGroup,
            pageable
        )

        if(!studentsPage.hasContent()){
            throw EntityNotFound("No students found at page $page with size $size for student search.")
        }

        return ResponseEntity.ok(studentModelAssembler.toCollectionModel(studentsPage))
    }
}
