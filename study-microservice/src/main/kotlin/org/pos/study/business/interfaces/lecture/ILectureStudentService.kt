package org.pos.study.business.interfaces.lecture

import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Student

interface ILectureStudentService {
    fun getStudentsByLecture(lectureId: String, page: Int, size: Int): Result<List<Student>>
    fun enrollStudentsInLecture(lectureId: String, studentIds: List<Long>): Result<Lecture>
    fun unenrollStudentsInLecture(lectureId: String, studentIds: List<Long>): Result<Lecture>
}