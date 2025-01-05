package org.pos.study.presentation.annotations

import api.academia.Auth

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(
    val roles: Array<Auth.Role>
)