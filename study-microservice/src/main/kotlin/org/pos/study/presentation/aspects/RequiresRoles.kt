package org.pos.study.presentation.aspects

import api.academia.Auth

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(
    val roles: Array<Auth.Role>
)