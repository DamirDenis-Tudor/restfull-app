package org.pos

import io.grpc.ServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import org.ktorm.database.Database
import org.pos.services.AuthService
import org.pos.services.BlackListService
import org.pos.services.TokenService

fun main() = runBlocking {
    val dbHost = System.getenv("AUTH_DB_HOST") ?: "0.0.0.0"
    val dbPort = System.getenv("AUTH_DB_PORT")?.toInt() ?: 3307
    val dbName = System.getenv("AUTH_DB_NAME") ?: "auth-database"
    val dbUser = System.getenv("AUTH_DB_USER") ?: "user"
    val dbPassword = System.getenv("AUTH_DB_PASSWORD") ?: "password"

    val jdbcUrl = "jdbc:mysql://$dbHost:$dbPort/$dbName"

    val database = Database.connect(
        url = jdbcUrl,
        driver = "com.mysql.cj.jdbc.Driver",
        user = dbUser,
        password = dbPassword
    )

    val blackListService = BlackListService(database)
    val tokenService = TokenService(blackListService)
    val authService = AuthService(tokenService)

    val server = ServerBuilder
        .forPort(50051)
        .addService(authService)
        .executor(Dispatchers.IO.asExecutor())
        .build()

    server.start()
    server.awaitTermination()
}
