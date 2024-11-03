package org.pos.study.dto.student

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.domain.Student
import org.pos.study.dto.constraints.StudentConstraints

data class StudentCreate(
    @field:Size(
        min = StudentConstraints.FirstName.MIN_SIZE,
        max = StudentConstraints.FirstName.MAX_SIZE
    ) val firstName: String,

    @field:Size(
        min = StudentConstraints.LastName.MIN_SIZE,
        max = StudentConstraints.LastName.MAX_SIZE
    ) val lastName: String,

    @field:Size(
        min = StudentConstraints.Email.MIN_SIZE,
        max = StudentConstraints.Email.MAX_SIZE
    ) val email: String,

    @field:Min(StudentConstraints.StudyYear.MIN_VALUE)
    @field:Max(StudentConstraints.StudyYear.MAX_VALUE)
    val studyYear: Int,

    @field:Min(StudentConstraints.StudentGroup.MIN_VALUE)
    @field:Max(StudentConstraints.StudentGroup.MAX_VALUE)
    val studentGroup: Int,

    val cycleType: Student.CycleType,
)
