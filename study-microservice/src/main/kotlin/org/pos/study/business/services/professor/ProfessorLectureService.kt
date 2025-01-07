package org.pos.study.business.services.professor

import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.interfaces.professor.IProfessorLectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.ProfessorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ProfessorLectureService(
    private val professorRepository: ProfessorRepository,
    private val lectureRepository: LectureRepository
) : IProfessorLectureService {

    override fun getLecturesByProfessor(id: Long, page: Int, size: Int): Result<Page<Lecture>> = runCatching {
        val professor = professorRepository.findById(id).orElseThrow {
            throw EntityNotFound("Professor with ID $id not found.")
        }

        lectureRepository.findByProfessor(professor, PageRequest.of(page, size))
    }

    override fun getLectureByProfessor(id: Long, lectureId: String): Result<Lecture> = runCatching {
        professorRepository.findById(id).orElseThrow {
            throw EntityNotFound("Professor with ID $id not found.")
        }

        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        if (lecture.professor?.id?.toLong() != id)
            throw EntityNotFound("Professor with ID $id does not have a lecture with ID $lectureId.")

        lecture
    }

    override fun isProfessorOwnerOfLecture(id: String, lectureId: String): Result<Boolean> = runCatching {
        val professor = professorRepository.findById(id.toLong())
            .orElseThrow { EntityNotFound("Professor with ID $id not found.") }
        val lecture = lectureRepository.findById(lectureId)
            .orElseThrow { EntityNotFound("Lecture with ID $lectureId not found.") }

        lecture.professor?.id == professor.id
    }
}
