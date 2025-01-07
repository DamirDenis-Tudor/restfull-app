package org.pos.study.business.dto.login

import api.academia.Auth

data class LoginResponse (
    val token: String,
    val message: String,
    val role: Auth.Role
)