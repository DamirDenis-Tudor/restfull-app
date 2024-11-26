package org.pos.study.business.dto.lecture

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.pos.study.persistence.entities.Lecture
import org.pos.study.business.dto.constraints.LectureConstraints

data class LectureUpdate(
    @field:Size(
        min = LectureConstraints.LectureName.MIN_SIZE,
        max = LectureConstraints.LectureName.MAX_SIZE
    ) var lectureName: String?,

    @field:Min(LectureConstraints.StudyYear.MIN_VALUE)
    @field:Max(LectureConstraints.StudyYear.MAX_VALUE)
    var studyYear: Int?,

    var professorId: Long? = null,

    var lectureType: Lecture.LectureType? = null,
    var categoryType: Lecture.CategoryType? = null,
    var examinationType: Lecture.ExaminationType? = null,
)
