package org.pos.study.controllers.student

import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Student
import org.pos.study.dto.student.StudentUpdate
import org.pos.study.repositories.StudentRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students")
class StudentController(
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @GetMapping
    fun getAllStudents(
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<CollectionModel<EntityModel<Student>>> {
        val studentsPage: Page<Student> = studentRepository.findAll(pageable)
        val studentModels = studentModelAssembler.toCollectionModel(studentsPage)
        return ResponseEntity.ok(studentModels)
    }


    @GetMapping("/{id}")
    fun getStudent(@PathVariable id: Long): ResponseEntity<EntityModel<Student>> {
        return studentRepository.findById(id)
            .map { studentModelAssembler.toModel(it) }
            .map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @PostMapping
    fun createStudent(@RequestBody student: Student): ResponseEntity<EntityModel<*>> {
        return runCatching {
            studentRepository.save(student)
        }.fold(
            onSuccess = { savedStudent ->
                ResponseEntity.status(HttpStatus.CREATED).body(studentModelAssembler.toModel(savedStudent))
            },
            onFailure = { exception ->
                when (exception) {
                    is DataIntegrityViolationException -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(EntityModel.of(mapOf("message" to exception.message)))

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(EntityModel.of(mapOf("message" to exception.message)))
                }

            }
        )
    }

    @PatchMapping("/{id}")
    fun updateStudent(
        @PathVariable id: Long,
        @RequestBody studentUpdates: StudentUpdate
    ): ResponseEntity<EntityModel<*>> {
        val existingStudent = studentRepository.findById(id).orElse(null)

        return existingStudent?.let {

            studentUpdates.firstName?.let { existingStudent.firstName = it }
            studentUpdates.lastName?.let { existingStudent.lastName = it }
            studentUpdates.cycleType?.let { existingStudent.cycleType = it }
            studentUpdates.email?.let { existingStudent.email = it }
            studentUpdates.studyYear?.takeIf { it != 0 }?.let { existingStudent.studyYear = it }
            studentUpdates.studentGroup?.takeIf { it != 0 }?.let { existingStudent.studentGroup = it }

            runCatching { studentRepository.save(existingStudent) }
                .fold(
                    onSuccess = { updatedStudent ->
                        ResponseEntity.ok(studentModelAssembler.toModel(updatedStudent))
                    },
                    onFailure = { exception ->
                        when (exception) {
                            is DataIntegrityViolationException -> ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(EntityModel.of(mapOf("message" to exception.message)))

                            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(EntityModel.of(mapOf("message" to exception.message)))
                        }
                    }
                )
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Student with ID $id not found.")))
    }



    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long): ResponseEntity<EntityModel<*>> {
        return if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(EntityModel.of(mapOf("message" to "Student with ID $id not found.")))
        }
    }

}