package org.pos.study.presentation.annotations

import api.academia.Auth
import io.swagger.v3.oas.annotations.Parameter

@Parameter(hidden = true)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectId(
    val forRole: Auth.Role
)
