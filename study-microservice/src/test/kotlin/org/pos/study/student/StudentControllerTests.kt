package org.pos.study.controllers.student

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.pos.study.controllers.assemblers.StudentModelAssembler
import org.pos.study.domain.Student
import org.pos.study.dto.student.StudentUpdate
import org.pos.study.repositories.StudentRepository
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import java.util.*

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class StudentControllerTests {

    private val studentRepository: StudentRepository = mock(StudentRepository::class.java)
    private val studentModelAssembler: StudentModelAssembler = mock(StudentModelAssembler::class.java)
    private val studentController = StudentController(studentRepository, studentModelAssembler)

    @Test
    fun `should return all students`() {
        val pageable: Pageable = mock(Pageable::class.java)
        val students = listOf(
            Student(id = 1, firstName = "John", lastName = "Doe", cycleType = Student.CycleType.Licenta, email = "john.doe@example.com", studyYear = 1, studentGroup = 1)
        )
        val page = PageImpl(students, pageable, students.size.toLong())

        whenever(studentRepository.findAll(pageable)).thenReturn(page)
        whenever(studentModelAssembler.toCollectionModel(page)).thenReturn(CollectionModel.of(listOf()))

        val response: ResponseEntity<CollectionModel<EntityModel<Student>>> = studentController.getAllStudents(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `should return student by ID`() {
        val student = Student(id = 1, firstName = "John", lastName = "Doe", cycleType = Student.CycleType.Licenta, email = "john.doe@example.com", studyYear = 1, studentGroup = 1)

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(studentModelAssembler.toModel(student)).thenReturn(EntityModel.of(student))

        val response: ResponseEntity<EntityModel<Student>> = studentController.getStudent(1L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(student.firstName, response.body?.content?.firstName)
    }

    @Test
    fun `should return NOT FOUND for non-existent student ID`() {
        whenever(studentRepository.findById(1L)).thenReturn(Optional.empty())

        val response: ResponseEntity<EntityModel<Student>> = studentController.getStudent(1L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should create a new student`() {
        val student = Student(id = 1, firstName = "John", lastName = "Doe", cycleType = Student.CycleType.Licenta, email = "john.doe@example.com", studyYear = 1, studentGroup = 1)
        whenever(studentRepository.save(student)).thenReturn(student)
        whenever(studentModelAssembler.toModel(student)).thenReturn(EntityModel.of(student))

        val response: ResponseEntity<EntityModel<*>> = studentController.createStudent(student)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `should return CONFLICT when creating a student with data integrity violation`() {
        val student = Student(id = 1, firstName = "John", lastName = "Doe", cycleType = Student.CycleType.Licenta, email = "john.doe@example.com", studyYear = 1, studentGroup = 1)
        whenever(studentRepository.save(student)).thenThrow(DataIntegrityViolationException("Conflict"))

        val response: ResponseEntity<EntityModel<*>> = studentController.createStudent(student)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `should update an existing student`() {
        val existingStudent = Student(id = 1, firstName = "John", lastName = "Doe", cycleType = Student.CycleType.Licenta, email = "john.doe@example.com", studyYear = 1, studentGroup = 1)
        val studentUpdate = StudentUpdate(firstName = "Jane")

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent))
        whenever(studentRepository.save(existingStudent)).thenReturn(existingStudent)
        whenever(studentModelAssembler.toModel(existingStudent)).thenReturn(EntityModel.of(existingStudent))

        val response: ResponseEntity<EntityModel<*>> = studentController.updateStudent(1L, studentUpdate)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Jane", existingStudent.firstName)
    }

    @Test
    fun `should return NOT FOUND when updating a non-existent student`() {
        val studentUpdate = StudentUpdate(firstName = "Jane")

        whenever(studentRepository.findById(1L)).thenReturn(Optional.empty())

        val response: ResponseEntity<EntityModel<*>> = studentController.updateStudent(1L, studentUpdate)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should delete an existing student`() {
        whenever(studentRepository.existsById(1L)).thenReturn(true)

        val response: ResponseEntity<EntityModel<*>> = studentController.deleteStudent(1L)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `should return NOT FOUND when deleting a non-existent student`() {
        whenever(studentRepository.existsById(1L)).thenReturn(false)

        val response: ResponseEntity<EntityModel<*>> = studentController.deleteStudent(1L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
