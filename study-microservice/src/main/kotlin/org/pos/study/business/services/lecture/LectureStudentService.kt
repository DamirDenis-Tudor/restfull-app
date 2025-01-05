package org.pos.study.business.services.lecture

import org.pos.study.business.exceptions.EntityConflict
import org.pos.study.business.interfaces.lecture.ILectureStudentService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Student
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.StudentRepository
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.exceptions.EntityRangeUnsatisfiable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.runCatching

@Service
class LectureStudentService(
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository
) : ILectureStudentService {

    override fun getStudentsByLecture(lectureId: String, page: Int, size: Int): Result<List<Student>> = runCatching {
        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }
        val studentPage = studentRepository.findByLecturesContaining(lecture, PageRequest.of(page, size))
        studentPage.takeIf { it.hasContent() }?.content ?: throw EntityRangeUnsatisfiable("No students found for lectureId $lectureId.")
    }

    override fun enrollStudentsInLecture(lectureId: String, studentIds: List<Long>): Result<Lecture> = runCatching {
        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        val students = studentIds.map { studentId ->
            studentRepository.findById(studentId).orElseThrow {
                throw EntityNotFound("Student with ID $studentId not found.")
            }
        }

        students.forEach { student ->
            if (student in lecture.students)
                throw EntityConflict("Student with ID ${student.id} already enrolled in lecture with ID $lectureId.")
        }

        students.forEach { student ->
            student.lectures.add(lecture)
            lecture.students.add(student)

            studentRepository.save(student)
            lectureRepository.save(lecture)
        }

        lecture
    }

    override fun unenrollStudentsInLecture(lectureId: String, studentIds: List<Long>): Result<Lecture> = runCatching {
        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        val students = studentIds.map { studentId ->
            studentRepository.findById(studentId).orElseThrow {
                throw EntityNotFound("Student with ID $studentId not found.")
            }
        }

        students.forEach { student ->
            if (student !in lecture.students)
                throw EntityConflict("Student with ID ${student.id} is not enrolled in lecture with ID $lectureId.")
        }

        students.forEach { student ->
            student.lectures.remove(lecture)
            lecture.students.remove(student)

            studentRepository.save(student)
            lectureRepository.save(lecture)
        }

        lecture
    }

    override fun isStudentEnrolledInLecture(studentEmail: String, lectureId: String): Result<Boolean> = runCatching {
        val student = studentRepository.findStudentByEmail(email = studentEmail)
            .orElseThrow { EntityNotFound("Student with email $studentEmail not found.") }
        val lecture = lectureRepository.findById(lectureId)
            .orElseThrow { EntityNotFound("Lecture with ID $lectureId not found.") }

        lecture.students.contains(student)
    }
}
