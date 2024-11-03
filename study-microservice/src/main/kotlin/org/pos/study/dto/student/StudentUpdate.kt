package org.pos.study.dto.student

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.domain.Student


data class StudentUpdate(
    @field:Size(min = 3, max = 15) val firstName: String? = null,
    @field:Size(min = 3, max = 15) val lastName: String? = null,
    @field:Size(min = 3, max = 15) val email: String? = null,

    @field:Min(value = 1) @field:Max(value = 4) val studyYear: Int? = null,
    @field:Min(value = 1) @field:Max(value = 5000) val studentGroup: Int? = null,

    val cycleType: Student.CycleType? = null,
)