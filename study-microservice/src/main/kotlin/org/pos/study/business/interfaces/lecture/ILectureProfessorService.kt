package org.pos.study.business.interfaces.lecture

import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Professor

interface ILectureProfessorService {
    fun getProfessorByLecture(lectureId: String): Result<Professor>
    fun updateProfessorForLecture(lectureId: String, professorId: Long): Result<Lecture>
    fun isProfessorOwnerOfLecture(email: String, lectureId: String): Result<Unit>
}