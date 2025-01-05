package org.pos.study.config

import api.academia.AuthServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthGrpcConfig {

    @Value("\${spring.auth.host.address}")
    lateinit var authAddress: String

    @Bean
    fun channel(): ManagedChannel = ManagedChannelBuilder
        .forAddress(authAddress.split(":")[0], authAddress.split(":")[1].toInt())
        .usePlaintext()
        .build()

    @Bean
    fun blockingStub() : AuthServiceGrpcKt.AuthServiceCoroutineStub =
        AuthServiceGrpcKt.AuthServiceCoroutineStub(channel())
}