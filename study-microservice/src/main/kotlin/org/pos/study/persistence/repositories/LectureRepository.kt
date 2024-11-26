package org.pos.study.persistence.repositories

import jakarta.transaction.Transactional
import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Professor
import org.pos.study.persistence.entities.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LectureRepository : JpaRepository<Lecture, String> {
    fun findByProfessor(professor: Professor, pageable: Pageable): Page<Lecture>

    fun findByStudentsContaining(student: Student, pageable: Pageable): Page<Lecture>

    @Query("""
        SELECT l FROM Lecture l WHERE
        (:lectureName IS NULL OR LOWER(l.lectureName) LIKE LOWER(CONCAT('%', :lectureName, '%'))) AND
        (:studyYear IS NULL OR l.studyYear = :studyYear) AND
        (:lectureType IS NULL OR l.lectureType = :lectureType) AND
        (:categoryType IS NULL OR l.categoryType = :categoryType) AND
        (:examinationType IS NULL OR l.examinationType = :examinationType)
    """)
    fun findAllByCriteria(
        @Param("lectureName") lectureName: String?,
        @Param("studyYear") studyYear: Int?,
        @Param("lectureType") lectureType: Lecture.LectureType?,
        @Param("categoryType") categoryType: Lecture.CategoryType?,
        @Param("examinationType") examinationType: Lecture.ExaminationType?,
        pageable: Pageable
    ): Page<Lecture>

    @Modifying
    @Transactional
    @Query("UPDATE Lecture l SET l.professor = NULL WHERE l.professor.id = :professorId")
    fun setProfessorToNull(@Param("professorId") professorId: Long)
}
