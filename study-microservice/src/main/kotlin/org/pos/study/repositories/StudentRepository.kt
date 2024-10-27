package org.pos.study.repositories

import org.pos.study.domain.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "students", path = "students")
interface StudentRepository : JpaRepository<Student, Long>{
    fun findAllByDisciplinesIsNotEmpty(): List<Student?>
}