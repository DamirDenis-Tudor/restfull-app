package org.pos.study.controllers.student

import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Student
import org.pos.study.repositories.StudentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students/search")
class StudentSearchController(
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @GetMapping
    fun searchStudents(
        @RequestParam(required = false) firstName: String? = null,
        @RequestParam(required = false) lastName: String? = null,
        @RequestParam(required = false) email: String? = null,
        @RequestParam(required = false) cycleType: Student.CycleType? = null,
        @RequestParam(required = false) studyYear: Int? = null,
        @RequestParam(required = false) studentGroup: Int? = null,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "10") size: Int = 10
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
