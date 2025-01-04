package org.pos.study.business.interfaces.student

import org.pos.study.persistence.entities.Student
import org.pos.study.business.dto.student.StudentCreate
import org.pos.study.business.dto.student.StudentUpdate
import org.pos.study.presentation.aspects.InjectEmail
import org.springframework.data.domain.Page

interface IStudentService {
    fun getAllStudents(page: Int, size: Int): Result<Page<Student>>
    fun getStudentById(id: Long): Result<Student>
    fun getStudentByEmail(email: String): Result<Student>
    fun createStudent(studentCreate: StudentCreate): Result<Student>
    fun updateStudent(id: Long, studentUpdate: StudentUpdate): Result<Student>
    fun deleteStudent(id: Long): Result<Any>

    fun verifyStudent(id: Long, email: String): Result<Unit>
}
