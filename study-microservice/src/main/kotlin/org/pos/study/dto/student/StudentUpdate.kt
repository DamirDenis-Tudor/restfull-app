package org.pos.study.dto.student

import org.pos.study.domain.Student


data class StudentUpdate(
    val firstName: String? = null,
    val lastName: String? = null,
    val cycleType: Student.CycleType? = null,
    val email: String? = null,
    val studyYear: Int? = null,
    val studentGroup: Int? = null
)