package org.pos.study.student

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.pos.study.controllers.assemblers.LectureModelAssembler
import org.pos.study.controllers.student.StudentLectureController
import org.pos.study.domain.Lecture
import org.pos.study.domain.Professor
import org.pos.study.domain.Student
import org.pos.study.repositories.LectureRepository
import org.pos.study.repositories.StudentRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.CollectionModel
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import java.util.*

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class StudentLectureControllerTests {

    private val lectureRepository: LectureRepository = mock()
    private val studentRepository: StudentRepository = mock()
    private val lectureModelAssembler: LectureModelAssembler = mock()
    private val studentLectureController = StudentLectureController(lectureRepository, studentRepository, lectureModelAssembler, mock())

    private val professor = Professor(
        id = 1,
        firstName = "Dr.",
        lastName = "Smith",
        email = "dr.smith@example.com",
        affiliation = "University",
        graderType = Professor.GraderType.Profesor,
        associationType = Professor.AssociationType.Titular
    )

    @Test
    fun `should return lectures by student ID`() {
        val student = Student(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            cycleType = null,
            email = "john.doe@example.com",
            studyYear = 1,
            studentGroup = 1
        )
        val pageable: Pageable = mock()
        val lectures = listOf(
            Lecture(
                id = 1,
                lectureName = "Math 101",
                studyYear = 1,
                lectureType = Lecture.LectureType.Impusa,
                categoryType = Lecture.CategoryType.Domeniu,
                examinationType = Lecture.ExaminationType.Examen,
                professor = professor
            )
        )
        val page = PageImpl(lectures, pageable, lectures.size.toLong())

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(lectureRepository.findByStudentsContaining(student, pageable)).thenReturn(page)
        whenever(lectureModelAssembler.toCollectionModel(page, 1L)).thenReturn(CollectionModel.of(listOf()))

        val response = studentLectureController.getLecturesByStudent(1L, 0, 10)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `should return NOT FOUND for non-existent student ID when getting lectures`() {
        whenever(studentRepository.findById(1L)).thenReturn(Optional.empty())

        val response = studentLectureController.getLecturesByStudent(1L, 0, 10)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should return lecture by student ID and lecture ID`() {
        val student = Student(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            cycleType = null,
            email = "john.doe@example.com",
            studyYear = 1,
            studentGroup = 1
        )
        val lecture = Lecture(
            id = 1,
            lectureName = "Math 101",
            studyYear = 1,
            lectureType = Lecture.LectureType.Impusa,
            categoryType = Lecture.CategoryType.Domeniu,
            examinationType = Lecture.ExaminationType.Examen,
            professor = professor,
            students = mutableListOf(student)
        )

        whenever(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture))

        val response = studentLectureController.getLectureByStudent(1L, 1L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `should return NOT FOUND for lecture not found by student ID and lecture ID`() {
        whenever(lectureRepository.findById(1L)).thenReturn(Optional.empty())

        val response = studentLectureController.getLectureByStudent(1L, 1L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should enroll student in lecture`() {
        val student = Student(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            cycleType = null,
            email = "john.doe@example.com",
            studyYear = 1,
            studentGroup = 1
        )
        val lecture = Lecture(
            id = 1,
            lectureName = "Math 101",
            studyYear = 1,
            lectureType = Lecture.LectureType.Impusa,
            categoryType = Lecture.CategoryType.Domeniu,
            examinationType = Lecture.ExaminationType.Examen,
            professor = professor
        )

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture))

        val response = studentLectureController.enrollStudentInLecture(1L, 1L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertTrue(student.lectures.contains(lecture))
        assertTrue(lecture.students.contains(student))
    }

    @Test
    fun `should return NOT FOUND when enrolling in non-existent student or lecture`() {
        whenever(studentRepository.findById(1L)).thenReturn(Optional.empty())

        val response = studentLectureController.enrollStudentInLecture(1L, 1L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should unroll student from lecture`() {
        val student = Student(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            cycleType = null,
            email = "john.doe@example.com",
            studyYear = 1,
            studentGroup = 1
        )
        val lecture = Lecture(
            id = 1,
            lectureName = "Math 101",
            studyYear = 1,
            lectureType = Lecture.LectureType.Impusa,
            categoryType = Lecture.CategoryType.Domeniu,
            examinationType = Lecture.ExaminationType.Examen,
            professor = professor,
            students = mutableListOf(student)
        )

        student.lectures.add(lecture)

        whenever(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        whenever(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture))

        val response = studentLectureController.unrollStudentFromLecture(1L, 1L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertFalse(student.lectures.contains(lecture))
        assertFalse(lecture.students.contains(student))
    }

    @Test
    fun `should return NOT FOUND when unrolling from non-existent student or lecture`() {
        whenever(studentRepository.findById(1L)).thenReturn(Optional.empty())

        val response = studentLectureController.unrollStudentFromLecture(1L, 1L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
