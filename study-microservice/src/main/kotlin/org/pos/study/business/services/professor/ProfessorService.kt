package org.pos.study.business.services.professor

import org.pos.study.persistence.entities.Professor
import org.pos.study.persistence.repositories.LectureRepository
import org.pos.study.persistence.repositories.ProfessorRepository
import org.pos.study.business.dto.professor.ProfessorCreate
import org.pos.study.business.dto.professor.ProfessorUpdate
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.exceptions.EntityUnverifiable
import org.pos.study.business.interfaces.professor.IProfessorService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.getOrElse
import kotlin.jvm.optionals.getOrElse
import kotlin.runCatching

@Service
class ProfessorService(
    private val professorRepository: ProfessorRepository,
    private val lectureRepository: LectureRepository
) : IProfessorService {

    override fun getAllProfessors(page: Int, size: Int): Result<Page<Professor>> = runCatching {
        val professorPage = professorRepository.findAll(PageRequest.of(page, size))

        if (professorPage.hasContent())
            throw EntityNotFound("No professors found")

        professorPage
    }

    override fun getProfessorById(id: Long): Result<Professor> = runCatching {
        professorRepository.findById(id).orElseThrow {
            throw EntityNotFound("Professor with ID $id not found.")
        }
    }

    override fun getProfessorByEmail(email: String): Result<Professor> = runCatching {
         professorRepository.findProfessorByEmail(email).getOrElse {
            throw EntityNotFound("Professor with email $email not found")
        }
    }

    override fun createProfessor(professorCreate: ProfessorCreate): Result<Professor> = runCatching {
        val professor = Professor(
            firstName = professorCreate.firstName,
            lastName = professorCreate.lastName,
            email = professorCreate.email,
            affiliation = professorCreate.affiliation,
            associationType = professorCreate.associationType,
            graderType = professorCreate.graderType
        )
        professorRepository.save(professor)
    }

    override fun updateProfessor(id: Long, professorUpdates: ProfessorUpdate): Result<Professor> = runCatching {
        val currentProfessor = professorRepository.findById(id).orElseThrow {
            throw EntityNotFound("Professor with ID $id does not exist.")
        }

        val updatedProfessor = currentProfessor.copy(
            firstName = professorUpdates.firstName ?: currentProfessor.firstName,
            lastName = professorUpdates.lastName ?: currentProfessor.lastName,
            email = professorUpdates.email ?: currentProfessor.email,
            affiliation = professorUpdates.affiliation ?: currentProfessor.affiliation,
            graderType = professorUpdates.graderType ?: currentProfessor.graderType,
            associationType = professorUpdates.associationType ?: currentProfessor.associationType
        )

        professorRepository.save(updatedProfessor)
    }

    override fun deleteProfessor(id: Long): Result<Unit> = runCatching {
        if (!professorRepository.existsById(id))
            throw EntityNotFound("Professor with ID $id does not exist.")

        lectureRepository.setProfessorToNull(id)
        professorRepository.deleteById(id)
    }

    override fun verifyProfessor(
        id: Long,
        email: String
    ): Result<Unit> = runCatching {
        professorRepository.findById(id).getOrElse {
            throw EntityNotFound("Student with ID $id not found.")
        }.takeIf {
            it.email == email
        } ?: throw EntityUnverifiable("No student with id $id has email $email.")
    }
}
