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
    val database = Database.connect(
        url = "jdbc:mysql://0.0.0.0:3307/auth-database",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "user",
        password = "password"
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