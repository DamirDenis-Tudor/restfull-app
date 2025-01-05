package org.pos.study.presentation.annotations

import io.swagger.v3.oas.annotations.Parameter

@Parameter(hidden = true)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectAuthorizationHeader()
