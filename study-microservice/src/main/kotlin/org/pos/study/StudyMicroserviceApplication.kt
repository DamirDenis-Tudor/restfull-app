package org.pos.study

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.hateoas.config.EnableHypermediaSupport

@SpringBootApplication
class StudyMicroserviceApplication

fun main(args: Array<String>) {
    runApplication<StudyMicroserviceApplication>(*args)
}
