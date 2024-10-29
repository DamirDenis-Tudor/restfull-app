package org.pos.study.repositories

import org.pos.study.domain.Professor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProfessorRepository : JpaRepository<Professor, Long>{
    @Query("""
        SELECT p FROM Professor p WHERE
        (:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND
        (:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND
        (:email IS NULL OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND
        (:affiliation IS NULL OR LOWER(p.affiliation) LIKE LOWER(CONCAT('%', :affiliation, '%'))) AND
        (:graderType IS NULL OR p.graderType = :graderType) AND
        (:associationType IS NULL OR p.associationType = :associationType)
    """)
    fun findAllByCriteria(
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?,
        @Param("email") email: String?,
        @Param("affiliation") affiliation: String?,
        @Param("graderType") graderType: Professor.GraderType?,
        @Param("associationType") associationType: Professor.AssociationType?,
        pageable: Pageable
    ): Page<Professor>
}
