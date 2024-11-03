package org.pos.study.dto.constraints

sealed class LectureConstraints {
    data object Id {
        const val MIN_SIZE = 1
        const val MAX_SIZE = 3
    }

    data object LectureName {
        const val MIN_SIZE = 3
        const val MAX_SIZE = 10
    }

    data object StudyYear {
        const val MIN_VALUE = 1L
        const val MAX_VALUE = 4L
    }
}

