package org.pos.study.business.dto.constraints

sealed class StudentConstraints {
    data object Id {
        const val MIN_SIZE = 0L
        const val MAX_SIZE = Long.MAX_VALUE
    }

    data object FirstName {
        const val MIN_SIZE = 3
        const val MAX_SIZE = 15
    }

    data object LastName {
        const val MIN_SIZE = 3
        const val MAX_SIZE = 15
    }

    data object Email {
        const val MIN_SIZE = 3
        const val MAX_SIZE = 40
    }

    data object StudyYear {
        const val MIN_VALUE = 1L
        const val MAX_VALUE = 4L
    }

    data object StudentGroup {
        const val MIN_VALUE = 1L
        const val MAX_VALUE = 5000L
    }
}
