package org.pos.study.config

import api.academia.AuthServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress

@Configuration
class AuthGrpcConfig {

    @Value("\${spring.auth.hostname}")
    lateinit var authHostname: String

    @Bean
    fun channel(): ManagedChannel {
        val authAddress = InetAddress.getByName(authHostname).hostAddress

        println("Auth address: $authAddress")

        return ManagedChannelBuilder
            .forAddress(authAddress, 50051)
            .usePlaintext()
            .build()
    }

    @Bean
    fun blockingStub() : AuthServiceGrpcKt.AuthServiceCoroutineStub =
        AuthServiceGrpcKt.AuthServiceCoroutineStub(channel())
}