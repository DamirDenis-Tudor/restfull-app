package org.pos.study.dto.professor

import org.pos.study.domain.Professor

data class ProfessorUpdate(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val affiliation: String? = null,
    val graderType: Professor.GraderType? = null,
    val associationType: Professor.AssociationType? = null
)
