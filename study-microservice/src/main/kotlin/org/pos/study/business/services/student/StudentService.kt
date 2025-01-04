package org.pos.study.business.services.student

import org.pos.study.persistence.entities.Student
import org.pos.study.persistence.repositories.StudentRepository
import org.pos.study.business.dto.student.StudentCreate
import org.pos.study.business.dto.student.StudentUpdate
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.exceptions.EntityRangeUnsatisfiable
import org.pos.study.business.exceptions.EntityUnverifiable
import org.pos.study.business.interfaces.student.IStudentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class StudentService(
    private val studentRepository: StudentRepository
) : IStudentService {

    override fun getAllStudents(page: Int, size: Int): Result<Page<Student>> = runCatching {
        val studentPage = studentRepository.findAll(PageRequest.of(page, size))

        if (!studentPage.hasContent())
            throw EntityNotFound("No students found at page $page with size $size.")

        studentPage
    }

    override fun getStudentById(id: Long): Result<Student> = runCatching {
        studentRepository.findById(id).orElseThrow {
            EntityNotFound("Student with ID $id not found")
        }
    }

    override fun getStudentByEmail(email: String): Result<Student> = runCatching {
        studentRepository.findStudentByEmail(email).getOrElse {
            throw EntityNotFound("Student with email $email not found")
        }
    }

    override fun createStudent(studentCreate: StudentCreate): Result<Student> = runCatching {
        val student = Student(
            firstName = studentCreate.firstName,
            lastName = studentCreate.lastName,
            email = studentCreate.email,
            cycleType = studentCreate.cycleType,
            studyYear = studentCreate.studyYear,
            studentGroup = studentCreate.studentGroup
        )
        studentRepository.save(student)
    }

    override fun updateStudent(id: Long, studentUpdate: StudentUpdate): Result<Student> = runCatching {
        val existingStudent = studentRepository.findById(id).orElseThrow {
            EntityNotFound("Student with ID $id not found.")
        }

        val updatedStudent = existingStudent.copy(
            id = existingStudent.id,
            firstName = studentUpdate.firstName ?: existingStudent.firstName,
            lastName = studentUpdate.lastName ?: existingStudent.lastName,
            email = studentUpdate.email ?: existingStudent.email,
            cycleType = studentUpdate.cycleType ?: existingStudent.cycleType,
            studyYear = studentUpdate.studyYear ?: existingStudent.studyYear,
            studentGroup = studentUpdate.studentGroup ?: existingStudent.studentGroup
        )

        studentRepository.save(updatedStudent)
    }

    override fun deleteStudent(id: Long): Result<Any> = runCatching {
        val existingStudent = studentRepository.findById(id).orElseThrow {
            EntityNotFound("Student with ID $id not found.")
        }

        studentRepository.delete(existingStudent)

        return@runCatching
    }

    override fun verifyStudent(
        id: Long,
        email: String
    ): Result<Unit> = runCatching {
        studentRepository.findById(id).getOrElse {
            throw EntityNotFound("Student with ID $id not found.")
        }.takeIf {
            it.email == email
        } ?: throw EntityUnverifiable("No student with id $id has email $email.")
    }
}
