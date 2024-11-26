package org.pos.study.business.dto.constraints

sealed class ProfessorConstraints {
    data object Id {
        const val MIN_SIZE = 0L
        const val MAX_SIZE = Long.MAX_VALUE
    }

    data object FirstName {
        const val MIN_SIZE = 3
        const val MIN_SIZE_SEARCH = 1
        const val MAX_SIZE = 15
    }

    data object LastName {
        const val MIN_SIZE = 3
        const val MIN_SIZE_SEARCH = 1
        const val MAX_SIZE = 15
    }

    data object Email {
        const val MIN_SIZE = 3
        const val MIN_SIZE_SEARCH = 1
        const val MAX_SIZE = 10
    }

    data object Affiliation {
        const val MIN_SIZE = 3
        const val MIN_SIZE_SEARCH = 1
        const val MAX_SIZE = 10
    }
}
