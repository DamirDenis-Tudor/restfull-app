package org.pos.study.presentation.controllers.student

import api.academia.Auth
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.pos.study.persistence.entities.Student
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.persistence.repositories.StudentRepository
import org.pos.study.presentation.aspects.RequiresRoles
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/students/search")
class StudentSearchController(
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @GetMapping
    fun searchStudents(

        @Size(min = StudentConstraints.FirstName.MIN_SIZE, max = StudentConstraints.FirstName.MAX_SIZE)
        @RequestParam(required = false) firstName: String? = null,

        @Size(min = StudentConstraints.LastName.MIN_SIZE, max = StudentConstraints.LastName.MAX_SIZE)
        @RequestParam(required = false) lastName: String? = null,

        @Size(min = StudentConstraints.Email.MIN_SIZE, max = StudentConstraints.Email.MAX_SIZE)
        @RequestParam(required = false) email: String? = null,

        @RequestParam(required = false) cycleType: Student.CycleType? = null,

        @Min(StudentConstraints.StudyYear.MIN_VALUE)
        @Max(StudentConstraints.StudyYear.MAX_VALUE)
        @RequestParam(required = false) studyYear: Int? = null,

        @Min(StudentConstraints.StudentGroup.MIN_VALUE)
        @Max(StudentConstraints.StudentGroup.MAX_VALUE)
        @RequestParam(required = false) studentGroup: Int? = null,

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
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

        return ResponseEntity.ok(studentModelAssembler.toCollectionModel(studentsPage))
    }
}
