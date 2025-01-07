package org.pos.study.business.interfaces.professor

import org.pos.study.persistence.entities.Lecture
import org.springframework.data.domain.Page

interface IProfessorLectureService {
    fun getLecturesByProfessor(id: Long, page: Int, size: Int): Result<Page<Lecture>>
    fun getLectureByProfessor(id: Long, lectureId: String): Result<Lecture>
    fun isProfessorOwnerOfLecture(id: String, lectureId: String): Result<Boolean>
}