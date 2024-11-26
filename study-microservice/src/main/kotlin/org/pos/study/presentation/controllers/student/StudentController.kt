package org.pos.study.presentation.controllers.student

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.pos.study.persistence.entities.Student
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.dto.student.StudentCreate
import org.pos.study.business.dto.student.StudentUpdate
import org.pos.study.persistence.repositories.StudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/students")
class StudentController(
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @GetMapping
    fun getAllStudents(

        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()

    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        val studentsPage: Page<Student> = studentRepository.findAll(PageRequest.of(page, size))

        if (studentsPage.hasContent())
            return ResponseEntity.ok(studentModelAssembler.toCollectionModel(studentsPage))

        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No student collection with size $size and page $page found."
        )
    }


    @GetMapping("/{id}")
    fun getStudent(

        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long

    ): ResponseEntity<EntityModel<Student>> {
        val student = studentRepository.findById(id)

        if (student.isPresent)
            return ResponseEntity.ok(studentModelAssembler.toModel(student.get()))

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $id not found")
    }

    @PostMapping
    fun createStudent(

        @Valid @RequestBody studentCreate: StudentCreate

    ): ResponseEntity<EntityModel<*>> {
        val student = Student(
            firstName = studentCreate.firstName,
            lastName = studentCreate.lastName,
            email = studentCreate.email,
            cycleType = studentCreate.cycleType,
            studyYear = studentCreate.studyYear,
            studentGroup = studentCreate.studentGroup,
        )

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(studentModelAssembler.toModel(studentRepository.save(student)))
    }

    @PatchMapping("/{id}")
    fun updateStudent(

        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,
        @Valid @RequestBody studentUpdates: StudentUpdate

    ): ResponseEntity<EntityModel<*>> {
        val existingStudent = studentRepository.findById(id).getOrNull()

        return existingStudent?.apply {
            studentUpdates.firstName?.let { this@apply.firstName = it }
            studentUpdates.lastName?.let { this@apply.lastName = it }
            studentUpdates.cycleType?.let { this@apply.cycleType = it }
            studentUpdates.email?.let { this@apply.email = it }
            studentUpdates.studyYear?.takeIf { it != 0 }?.let { this@apply.studyYear = it }
            studentUpdates.studentGroup?.takeIf { it != 0 }?.let { this@apply.studentGroup = it }
        }?.let {
            ResponseEntity.ok(studentModelAssembler.toModel(studentRepository.save(it)))
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $id not found")
    }


    @DeleteMapping("/{id}")
    fun deleteStudent(

        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long

    ): ResponseEntity<EntityModel<*>> {
        if (studentRepository.existsById(id))
            return studentRepository.deleteById(id).let { ResponseEntity.noContent().build() }

        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id $id not found")
    }

}