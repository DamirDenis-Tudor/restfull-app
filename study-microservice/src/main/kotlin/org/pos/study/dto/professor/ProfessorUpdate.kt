package org.pos.study.dto.professor

import jakarta.validation.constraints.Size
import org.pos.study.domain.Professor

data class ProfessorUpdate(
    @field:Size(min = 3, max = 15) val firstName: String? = null,
    @field:Size(min = 3, max = 15) val lastName: String? = null,
    @field:Size(min = 3, max = 10) val email: String? = null,
    @field:Size(min = 3, max = 10) val affiliation: String? = null,

    val graderType: Professor.GraderType? = null,
    val associationType: Professor.AssociationType? = null
)