package org.pos.study.presentation.handlers

import org.pos.study.business.exceptions.EntityConflict
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.exceptions.EntityRangeUnsatisfiable
import org.pos.study.business.exceptions.EntityUnverifiable
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class EntityExceptionHandler {

    @ExceptionHandler(EntityRangeUnsatisfiable::class)
    fun handleDataIntegrityViolation(ex: EntityRangeUnsatisfiable): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(EntityUnverifiable::class)
    fun handleDataIntegrityViolation(ex: EntityUnverifiable): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(EntityConflict::class)
    fun handleDataIntegrityViolation(ex: EntityConflict): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(EntityNotFound::class)
    fun handleDataIntegrityViolation(ex: EntityNotFound): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }
}
