package org.pos.study.repositories

import org.pos.study.domain.Teacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "teacher", path = "professors")
interface TeacherRepository : JpaRepository<Teacher, Long>
