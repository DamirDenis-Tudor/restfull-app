package org.pos.study

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
    info = Info(
        title = "Study Microservice API",
        version = "1.0.0",
        description = "This is the API for the Study Microservice, providing information about the service."
    )
)
@SpringBootApplication
class StudyMicroserviceApplication

fun main(args: Array<String>) {
    runApplication<StudyMicroserviceApplication>(*args)
}
