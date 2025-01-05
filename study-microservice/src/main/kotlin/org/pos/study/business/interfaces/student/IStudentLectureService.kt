package org.pos.study.business.interfaces.student

import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Student
import org.springframework.data.domain.Page

interface IStudentLectureService {
    fun getLecturesByStudent(studentId: Long, page: Int, size: Int): Result<Page<Lecture>>
    fun getLectureByStudent(studentId: Long, lectureId: String): Result<Lecture>
    fun enrollStudentInLecture(studentId: Long, lectureId: String): Result<Lecture>
    fun unrollStudentFromLecture(studentId: Long, lectureId: String): Result<Student>
}
