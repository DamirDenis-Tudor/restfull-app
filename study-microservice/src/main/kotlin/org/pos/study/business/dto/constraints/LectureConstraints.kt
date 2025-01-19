package org.pos.study.business.dto.constraints

sealed class LectureConstraints {
    data object Id {
        const val MIN_SIZE = 100L
        const val MAX_SIZE = 999L
    }

    data object LectureName {
        const val MIN_SIZE = 3
        const val MAX_SIZE = 30
    }

    data object StudyYear {
        const val MIN_VALUE = 1L
        const val MAX_VALUE = 4L
    }
}

