package org.pos.study

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudyMicroserviceApplication

fun main(args: Array<String>) {
    runApplication<StudyMicroserviceApplication>(*args)
}
