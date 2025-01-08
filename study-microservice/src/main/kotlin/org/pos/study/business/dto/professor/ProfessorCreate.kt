package org.pos.study.business.dto.professor

import jakarta.validation.constraints.Size
import org.pos.study.persistence.entities.Professor
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.springframework.validation.annotation.Validated

data class ProfessorCreate(
    @field:Size(
        min = ProfessorConstraints.FirstName.MIN_SIZE,
        max = ProfessorConstraints.FirstName.MAX_SIZE
    ) val firstName: String,

    @field:Size(
        min = ProfessorConstraints.LastName.MIN_SIZE,
        max = ProfessorConstraints.LastName.MAX_SIZE
    ) val lastName: String,

    @field:Size(
        min = ProfessorConstraints.Email.MIN_SIZE,
        max = ProfessorConstraints.Email.MAX_SIZE
    ) val email: String,

    @field:Size(
        min = ProfessorConstraints.Affiliation.MIN_SIZE,
        max = ProfessorConstraints.Affiliation.MAX_SIZE
    ) val affiliation: String,

    val graderType: Professor.GraderType,
    val associationType: Professor.AssociationType
)