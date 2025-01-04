package org.pos.study.presentation.aspects

import api.academia.Auth

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectEmail(
    val forRole: Auth.Role
)