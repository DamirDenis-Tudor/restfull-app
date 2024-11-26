package org.pos.study.business.services.lecture

import org.pos.study.business.dto.lecture.LectureCreate
import org.pos.study.business.dto.lecture.LectureUpdate
import org.pos.study.business.exceptions.EntityConflict
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.interfaces.lecture.ILectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.ProfessorRepository
import org.pos.study.persistence.repositories.StudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository
) : ILectureService {

    override fun getLectures(page: Int, size: Int): Result<Page<Lecture>> = runCatching {
        val lecturePage = lectureRepository.findAll(PageRequest.of(page, size))
        lecturePage.takeIf { it.hasContent() }
            ?: throw EntityNotFound("Lectures not found at page $page with size $size")
    }

    override fun getLectureById(lectureId: String): Result<Lecture> =
        lectureRepository.findById(lectureId)
            .map { Result.success(it) }
            .orElse(Result.failure(EntityNotFound("Lecture with ID $lectureId not found.")))

    override fun createLecture(lectureCreate: LectureCreate): Result<Lecture> = runCatching {
        val professor = professorRepository.findById(lectureCreate.professorId).getOrNull()
            ?: throw EntityNotFound("Professor with ID ${lectureCreate.professorId} not found.")

        if (lectureRepository.existsById(lectureCreate.id))
            throw EntityConflict("Lecture with ID ${lectureCreate.id} already exists.")

        val newLecture = Lecture(
            id = lectureCreate.id,
            lectureName = lectureCreate.lectureName,
            studyYear = lectureCreate.studyYear,
            lectureType = lectureCreate.lectureType,
            categoryType = lectureCreate.categoryType,
            examinationType = lectureCreate.examinationType,
            professor = professor
        )

        lectureRepository.save(newLecture)
    }

    override fun updateLecture(lectureId: String, lectureUpdate: LectureUpdate): Result<Lecture> = runCatching {
        val existingLecture = lectureRepository.findById(lectureId).orElseThrow {
            EntityNotFound("Lecture with ID $lectureId not found.")
        }

        val professor = lectureUpdate.professorId?.let {
            professorRepository.findById(it).getOrNull()
                ?: throw EntityNotFound("Professor with ID ${lectureUpdate.professorId} not found.")
        }

        val updatedLecture = existingLecture.copy(
            lectureName = lectureUpdate.lectureName ?: existingLecture.lectureName,
            studyYear = lectureUpdate.studyYear ?: existingLecture.studyYear,
            lectureType = lectureUpdate.lectureType ?: existingLecture.lectureType,
            categoryType = lectureUpdate.categoryType ?: existingLecture.categoryType,
            examinationType = lectureUpdate.examinationType ?: existingLecture.examinationType,
            professor = professor ?: existingLecture.professor
        )

        lectureRepository.save(updatedLecture)
    }

    override fun deleteLecture(lectureId: String): Result<Unit> = runCatching {
        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            EntityNotFound("Lecture with ID $lectureId not found.")
        }

        lecture.students.forEach { student ->
            student.lectures.remove(lecture)
            studentRepository.save(student)
        }

        lectureRepository.deleteById(lectureId)
    }
}
