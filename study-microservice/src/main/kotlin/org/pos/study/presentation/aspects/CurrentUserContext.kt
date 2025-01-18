package org.pos.study.presentation.aspects

import api.academia.Auth

object CurrentUserContext {
    private val userData = ThreadLocal<Pair<Pair<Auth.Role, String>, String>>()

    fun setData(data: Pair<Pair<Auth.Role, String>, String>) {
        userData.set(data)
    }

    fun getRole(): Auth.Role {
        return userData.get().first.first
    }

    fun getId(): String {
        return userData.get().first.second
    }

    fun getToken(): String {
        return userData.get().second
    }

    fun clear() {
        userData.remove()
    }
}