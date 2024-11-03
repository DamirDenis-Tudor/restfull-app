package org.pos.study.dto.professor

import jakarta.validation.constraints.Size
import org.pos.study.domain.Professor
import org.pos.study.dto.constraints.ProfessorConstraints

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