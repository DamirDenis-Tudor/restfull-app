package org.pos.study.business.dto.student

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.persistence.entities.Student
import org.pos.study.business.dto.constraints.StudentConstraints

data class StudentUpdate(
    @field:Size(
        min = StudentConstraints.FirstName.MIN_SIZE,
        max = StudentConstraints.FirstName.MAX_SIZE
    ) val firstName: String? = null,

    @field:Size(
        min = StudentConstraints.LastName.MIN_SIZE,
        max = StudentConstraints.LastName.MAX_SIZE
    ) val lastName: String? = null,

    @field:Size(
        min = StudentConstraints.Email.MIN_SIZE,
        max = StudentConstraints.Email.MAX_SIZE
    ) val email: String? = null,

    @field:Min(StudentConstraints.StudyYear.MIN_VALUE)
    @field:Max(StudentConstraints.StudyYear.MAX_VALUE)
    val studyYear: Int? = null,

    @field:Min(StudentConstraints.StudentGroup.MIN_VALUE)
    @field:Max(StudentConstraints.StudentGroup.MAX_VALUE)
    val studentGroup: Int? = null,

    val cycleType: Student.CycleType? = null,
)
