package org.pos.study.repositories

import org.pos.study.domain.Professor
import org.springframework.data.jpa.repository.JpaRepository

interface TeacherRepository : JpaRepository<Professor, Long>
