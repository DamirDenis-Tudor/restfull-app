package org.pos

import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking

class AuthServiceGrpc : AuthServiceGrpcKt.AuthServiceCoroutineImplBase() {
    override suspend fun authenticate(request: Auth.AuthRequest): Auth.AuthResponse {
        val dummyToken = "dummyToken123"

        val response = Auth.AuthResponse.newBuilder()
            .setToken(dummyToken)
            .build()

        return response
    }
}

fun main(): Unit = runBlocking {
    ServerBuilder
        .forPort(50051)
        .addService(AuthServiceGrpc())
        .executor(Dispatchers.IO.asExecutor())
        .build()
        .apply(Server::start)
        .apply(Server::awaitTermination)
}
