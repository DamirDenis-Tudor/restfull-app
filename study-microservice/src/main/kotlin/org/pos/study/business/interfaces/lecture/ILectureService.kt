package org.pos.study.business.interfaces.lecture

import org.pos.study.business.dto.lecture.LectureCreate
import org.pos.study.business.dto.lecture.LectureUpdate
import org.pos.study.persistence.entities.Lecture
import org.springframework.data.domain.Page

interface ILectureService {
    fun getLectures(page: Int, size: Int): Result<Page<Lecture>>
    fun getLectureById(lectureId: String): Result<Lecture>
    fun createLecture(lectureCreate: LectureCreate): Result<Lecture>
    fun updateLecture(lectureId: String, lectureUpdate: LectureUpdate): Result<Lecture>
    fun deleteLecture(lectureId: String): Result<Unit>
}