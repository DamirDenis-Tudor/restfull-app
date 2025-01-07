package org.pos.study.presentation.aspects

import api.academia.Auth

object CurrentUserContext {
    private val userData = ThreadLocal<Pair<Auth.Role, String>>()

    fun setData(data: Pair<Auth.Role, String>) {
        userData.set(data)
    }

    fun getRole(): Auth.Role {
        return userData.get().first
    }

    fun getId(): String {
        return userData.get().second
    }

    fun clear() {
        userData.remove()
    }
}