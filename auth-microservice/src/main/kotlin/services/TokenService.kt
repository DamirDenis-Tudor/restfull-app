package org.pos.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.pos.entities.User
import org.pos.exceptions.TokenBlackedListedException
import java.util.*

class TokenService(private val persistence: BlackListService) {

    private val jwtSecret = "your-256-bit-secret"

    fun login(username: String, password: String): Result<Pair<String, Int>> {
        val user = persistence.validateUserCredentials(username, password)
        return user.map {generateJwt(it) to (user.getOrNull()?.role ?: 0) }
    }

    private fun generateJwt(user: User): String {
        val algorithm = Algorithm.HMAC256(jwtSecret)
        return JWT.create()
            .withIssuer("http://localhost:50051")
            .withSubject(user.email)
            .withClaim("role", user.role)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
            .withJWTId(UUID.randomUUID().toString())
            .sign(algorithm)
    }

    fun validateToken(token: String): Result<DecodedJWT> {
        return runCatching {
            if (persistence.isTokenBlacklisted(token).getOrElse { throw it })
                throw TokenBlackedListedException("Token is on the blacklist.")

            val verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer("http://localhost:50051")
                .build()

            verifier.verify(token)
        }
    }

    fun invalidateToken(token: String): Result<Unit> {
        return persistence.addToBlacklist(token)
    }
}