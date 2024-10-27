package org.pos.study.repositories

import org.pos.study.domain.Discipline
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "lectures", path = "lectures")
interface DisciplineRepository: JpaRepository<Discipline, Long>