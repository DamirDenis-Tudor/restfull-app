package org.pos.study.dto.lecture

import org.pos.study.domain.Lecture

data class LectureUpdate(
    var lectureName: String?,
    var studyYear: Int?,
    var lectureType: Lecture.LectureType?,
    var categoryType: Lecture.CategoryType?,
    var examinationType: Lecture.ExaminationType?,
    var professorId: Long?
)
