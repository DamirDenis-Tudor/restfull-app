package org.pos.study.business.interfaces.professor

import org.pos.study.business.dto.professor.ProfessorCreate
import org.pos.study.business.dto.professor.ProfessorUpdate
import org.pos.study.persistence.entities.Professor
import org.pos.study.presentation.aspects.InjectEmail
import org.springframework.data.domain.Page

interface IProfessorService {
    fun getAllProfessors(page: Int, size: Int): Result<Page<Professor>>
    fun getProfessorById(id: Long): Result<Professor>
    fun getProfessorByEmail(email: String): Result<Professor>
    fun createProfessor(professorCreate: ProfessorCreate): Result<Professor>
    fun updateProfessor(id: Long, professorUpdates: ProfessorUpdate): Result<Professor>
    fun deleteProfessor(id: Long): Result<Unit>

    fun verifyProfessor(id: Long, email: String): Result<Unit>
}