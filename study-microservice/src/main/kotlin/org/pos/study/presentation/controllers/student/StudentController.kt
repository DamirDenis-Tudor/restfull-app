package org.pos.study.presentation.controllers.student

import api.academia.Auth
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.pos.study.business.dto.constraints.PageConstraints
import org.pos.study.business.dto.constraints.StudentConstraints
import org.pos.study.business.dto.student.StudentCreate
import org.pos.study.business.dto.student.StudentUpdate
import org.pos.study.business.interfaces.student.IStudentService
import org.pos.study.persistence.entities.Student
import org.pos.study.presentation.aspects.InjectEmail
import org.pos.study.presentation.aspects.RequiresRoles
import org.pos.study.presentation.assemblers.StudentModelAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students")
class StudentController(
    private val studentService: IStudentService,
    private val studentModelAssembler: StudentModelAssembler
) {
    @GetMapping
    @RequiresRoles(roles = [Auth.Role.ADMIN])
    fun getAllStudents(
        @Min(PageConstraints.Page.MIN_VALUE)
        @Max(PageConstraints.Page.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Page.DEFAULT_VALUE}")
        page: Int = PageConstraints.Page.DEFAULT_VALUE.toInt(),

        @Min(PageConstraints.Size.MIN_VALUE)
        @Max(PageConstraints.Size.MAX_VALUE)
        @RequestParam(defaultValue = "${PageConstraints.Size.DEFAULT_VALUE}")
        size: Int = PageConstraints.Size.DEFAULT_VALUE.toInt()
    ): ResponseEntity<CollectionModel<EntityModel<Student>>> =
         studentService.getAllStudents(page, size).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toCollectionModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN, Auth.Role.STUDENT])
    @GetMapping("/{id}")
    fun getStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,

        @InjectEmail(forRole = Auth.Role.STUDENT)
        email: String
    ): ResponseEntity<EntityModel<Student>> {
        email.takeIf{it.isNotBlank()}?.let {
            studentService.verifyStudent(id, email).getOrThrow()
        }

        return studentService.getStudentById(id).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.STUDENT])
    @GetMapping("/me")
    fun getCurrentStudent(
        @InjectEmail(forRole = Auth.Role.STUDENT)
        email: String
    ): ResponseEntity<EntityModel<Student>> {
        return studentService.getStudentByEmail(email).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }
    }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @PostMapping
    fun createStudent(
        @Valid @RequestBody studentCreate: StudentCreate
    ): ResponseEntity<EntityModel<Student>> =
         studentService.createStudent(studentCreate).getOrThrow()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(studentModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @PatchMapping("/{id}")
    fun updateStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long,
        @Valid @RequestBody studentUpdate: StudentUpdate
    ): ResponseEntity<EntityModel<Student>> =
         studentService.updateStudent(id, studentUpdate).getOrThrow()
            .let { ResponseEntity.ok(studentModelAssembler.toModel(it)) }

    @RequiresRoles(roles = [Auth.Role.ADMIN])
    @DeleteMapping("/{id}")
    fun deleteStudent(
        @Min(StudentConstraints.Id.MIN_SIZE)
        @Max(StudentConstraints.Id.MAX_SIZE)
        @PathVariable id: Long
    ): ResponseEntity<Void> =
        studentService.deleteStudent(id).getOrThrow()
            .let { ResponseEntity.noContent().build() }
}
