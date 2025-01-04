package org.pos.study.persistence.repositories

import org.pos.study.persistence.entities.Lecture
import org.pos.study.persistence.entities.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StudentRepository : JpaRepository<Student, Long> {
    fun findStudentByEmail(email: String): Result<Student>
    fun findByLecturesContaining(student: Lecture, pageable: Pageable): Page<Student>

    @Query("""
        SELECT s FROM Student s WHERE
        (:firstName IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND
        (:lastName IS NULL OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND
        (:email IS NULL OR LOWER(s.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND
        (:cycleType IS NULL OR s.cycleType = :cycleType) AND
        (:studyYear IS NULL OR s.studyYear = :studyYear) AND
        (:studentGroup IS NULL OR s.studentGroup = :studentGroup)
    """)
    fun findAllByCriteria(
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?,
        @Param("email") email: String?,
        @Param("cycleType") cycleType: Student.CycleType?,
        @Param("studyYear") studyYear: Int?,
        @Param("studentGroup") studentGroup: Int?,
        pageable: Pageable
    ): Page<Student>

}
