package org.pos.study.repositories

import org.pos.study.domain.Lecture
import org.springframework.data.jpa.repository.JpaRepository

interface DisciplineRepository: JpaRepository<Lecture, Long>