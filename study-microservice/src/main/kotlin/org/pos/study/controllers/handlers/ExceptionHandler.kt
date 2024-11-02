package org.pos.study.controllers.handlers

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleValidationExceptions(ex: MethodArgumentTypeMismatchException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleValidationExceptions(ex: HttpMessageNotReadableException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidationExceptions(ex: HandlerMethodValidationException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("message" to ex.reason)))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<*> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("errors" to errors, "message" to "Validation failed")))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(EntityModel.of(mapOf("message" to "Data integrity violation.")))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleLectureNotFound(ex: ResponseStatusException): ResponseEntity<*> {
        return ResponseEntity.status(ex.statusCode)
            .body(EntityModel.of(mapOf("message" to ex.reason)))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(EntityModel.of(mapOf("message" to "An unexpected error occurred.")))
    }
}
