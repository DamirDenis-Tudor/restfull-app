package org.pos.services

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.any
import org.ktorm.entity.find
import org.pos.entities.BlacklistToken
import org.pos.entities.User
import org.pos.entities.blacklistTokens
import org.pos.entities.users
import org.pos.exceptions.InvalidCredentialsException
import org.pos.exceptions.UserNotFoundException
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

class BlackListService(private val db: Database) {

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(token.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    fun validateUserCredentials(username: String, password: String): Result<User> = runCatching {

        val user = db.users().find { it.email eq username }
            ?: throw UserNotFoundException("User with username $username not found")

        if (user.password != password) {
            throw InvalidCredentialsException("Invalid credentials for user $username")
        }

        user
    }

    fun addToBlacklist(tkn: String): Result<Unit> = runCatching {
        if (db.blacklistTokens().find { it.token eq hashToken(tkn) } != null)
            return@runCatching

        db.useTransaction {
            db.blacklistTokens().add(
                BlacklistToken {
                    token = hashToken(tkn)
                    timestamp = LocalDateTime.now()
                }
            )
        }
    }

    fun isTokenBlacklisted(token: String): Result<Boolean> = runCatching {
        db.blacklistTokens().any { it.token eq hashToken(token) }
    }
}


