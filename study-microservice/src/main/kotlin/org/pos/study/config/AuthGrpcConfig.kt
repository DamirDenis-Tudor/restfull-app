package org.pos.study.config

import api.academia.AuthServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthGrpcConfig {

    @Bean
    fun channel(): ManagedChannel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    @Bean
    fun blockingStub() : AuthServiceGrpcKt.AuthServiceCoroutineStub =
        AuthServiceGrpcKt.AuthServiceCoroutineStub(channel())
}