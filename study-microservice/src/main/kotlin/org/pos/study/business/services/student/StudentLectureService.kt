package org.pos.study.business.services.student

import org.pos.study.business.exceptions.EntityConflict
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.exceptions.EntityRangeUnsatisfiable
import org.pos.study.business.interfaces.student.IStudentLectureService
import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Student
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.StudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class StudentLectureService(
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository
) : IStudentLectureService {

    override fun getLecturesByStudent(studentId: Long, page: Int, size: Int): Result<Page<Lecture>> = runCatching {
        val student = studentRepository.findById(studentId).orElseThrow {
            throw EntityNotFound("Student with ID $studentId not found.")
        }

        lectureRepository.findByStudentsContaining(student, PageRequest.of(page, size)).also {
            if(!it.hasContent()) throw EntityNotFound("No lectures found at page $page with size $size for student $studentId.")
        }
    }

    override fun getLectureByStudent(studentId: Long, lectureId: String): Result<Lecture> = runCatching {
        val student = studentRepository.findById(studentId).orElseThrow {
            throw EntityNotFound("Student with ID $studentId not found.")
        }

        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        if (!lecture.students.contains(student))
            throw EntityNotFound("Lecture with ID $lectureId is not associated with student ID $studentId.")

        lecture
    }

    override fun enrollStudentInLecture(studentId: Long, lectureId: String): Result<Lecture> = runCatching {
        val student = studentRepository.findById(studentId).orElseThrow {
            throw EntityNotFound("Student with ID $studentId not found.")
        }

        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        if (student.lectures.contains(lecture)) {
            throw EntityConflict("Student with id $studentId is already enrolled on lecture with id $lectureId.")
        }

        student.lectures.add(lecture)
        lecture.students.add(student)
        studentRepository.save(student)
        lectureRepository.save(lecture)

        lecture
    }

    override fun unrollStudentFromLecture(studentId: Long, lectureId: String): Result<Student> = runCatching {
        val student = studentRepository.findById(studentId).orElseThrow {
            throw EntityNotFound("Student with ID $studentId not found.")
        }

        val lecture = lectureRepository.findById(lectureId).orElseThrow {
            throw EntityNotFound("Lecture with ID $lectureId not found.")
        }

        if (!student.lectures.contains(lecture)) {
            throw EntityNotFound("Student with id $studentId is not enrolled on lecture with id $lectureId.")
        }

        student.lectures.remove(lecture)
        lecture.students.remove(student)
        studentRepository.save(student)
        lectureRepository.save(lecture)

        student
    }
}
