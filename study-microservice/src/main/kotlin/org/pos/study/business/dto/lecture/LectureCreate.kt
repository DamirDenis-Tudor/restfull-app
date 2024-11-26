package org.pos.study.business.dto.lecture

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.business.dto.constraints.LectureConstraints
import org.pos.study.business.dto.constraints.ProfessorConstraints
import org.pos.study.persistence.entities.Lecture

data class LectureCreate(
    @field:Size(min = LectureConstraints.Id.MIN_SIZE, max = LectureConstraints.Id.MAX_SIZE)
    var id: String,

    @field:Min(value = ProfessorConstraints.Id.MIN_SIZE)
    var professorId: Long,

    @field:Size(
        min = LectureConstraints.LectureName.MIN_SIZE,
        max = LectureConstraints.LectureName.MAX_SIZE
    ) var lectureName: String,

    @field:Min(value = LectureConstraints.StudyYear.MIN_VALUE)
    @field:Max(value = LectureConstraints.StudyYear.MAX_VALUE)
    var studyYear: Int,

    var lectureType: Lecture.LectureType,
    var categoryType: Lecture.CategoryType,
    var examinationType: Lecture.ExaminationType,
)
