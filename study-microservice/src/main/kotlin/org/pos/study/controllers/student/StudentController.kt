package org.pos.study.controllers

import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Student
import org.pos.study.repositories.StudentRepository
import org.springframework.hateoas.EntityModel
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/academia/students")
class StudentController(
    private val studentRepository: StudentRepository,
    private val studentModelAssembler: StudentModelAssembler
) {

    @GetMapping("/{id}")
    fun getStudent(@PathVariable id: Long): EntityModel<Student>? =
        studentRepository.findById(id).get().let { studentModelAssembler.toModel(it) }

    @PostMapping
    fun createStudent(@RequestBody student: Student): EntityModel<Student> {
        return studentModelAssembler.toModel(studentRepository.save(student))
    }

    @PutMapping("/{id}")
    fun updateStudent(@PathVariable id: Long, @RequestBody student: Student): EntityModel<Student>? {
        val updatedStudent = student.copy(id = studentRepository.findById(id).get().id)
        return studentModelAssembler.toModel(studentRepository.save(updatedStudent))
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long) {
        studentRepository.deleteById(id)
    }
}
