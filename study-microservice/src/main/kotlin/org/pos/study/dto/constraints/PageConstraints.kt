package org.pos.study.dto.constraints

sealed class PageConstraints {
    object Page {
        const val MIN_VALUE = 0L
        const val DEFAULT_VALUE = 0L
    }

    object Size {
        const val MIN_VALUE = 1L
        const val MAX_VALUE = 30L
        const val DEFAULT_VALUE = 5L
    }
}
