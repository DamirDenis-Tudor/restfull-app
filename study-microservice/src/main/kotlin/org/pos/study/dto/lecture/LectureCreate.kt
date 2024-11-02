package org.pos.study.dto.lecture

import jakarta.validation.constraints.Size
import org.pos.study.domain.Lecture

data class LectureCreate(
    @field:Size(min = 3, max = 3)
    var id: String,
    @field:Size(min = 3, max = 10)
    var lectureName: String,
    @field:Size(min = 1, max = 4)
    var studyYear: Int,
    var lectureType: Lecture.LectureType,
    var categoryType: Lecture.CategoryType,
    var examinationType: Lecture.ExaminationType,
    var professorId: Long
)
