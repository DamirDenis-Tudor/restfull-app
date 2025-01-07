package org.pos.study.business.interfaces.lecture

import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Student
import org.springframework.data.domain.Page

interface ILectureStudentService {
    fun getStudentsByLecture(lectureId: String, page: Int, size: Int): Result<Page<Student>>
    fun enrollStudentsInLecture(lectureId: String, studentIds: List<Long>): Result<Lecture>
    fun unenrollStudentsInLecture(lectureId: String, studentIds: List<Long>): Result<Lecture>
    fun isStudentEnrolledInLecture(id: String, lectureId: String): Result<Boolean>
}