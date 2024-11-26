package org.pos.study.business.services.lecture

import org.pos.study.business.exceptions.EntityConflict
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.interfaces.lecture.ILectureProfessorService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Professor
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.ProfessorRepository
import org.springframework.stereotype.Service

@Service
class LectureProfessorService(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository
) : ILectureProfessorService {

    override fun getProfessorByLecture(lectureId: String): Result<Professor> = runCatching {
        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }
        lecture.professor ?: throw EntityNotFound("Lecture with ID $lectureId has no professor.")
    }

    override fun updateProfessorForLecture(lectureId: String, professorId: Long): Result<Lecture> = runCatching {
        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        val professor = professorRepository.findById(professorId).orElseThrow {
            throw EntityNotFound("Professor with ID $professorId not found.")
        }

        if (lecture.professor == professor)
            throw EntityConflict("Professor with ID $professorId is already owner of lecture with id $lectureId.")

        lecture.professor = professor
        lectureRepository.save(lecture)
    }
}