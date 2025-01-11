package org.pos.study.presentation.handlers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.pos.study.business.exceptions.EntityConflict
import org.pos.study.business.exceptions.EntityNotFound
import org.pos.study.business.exceptions.EntityRangeUnsatisfiable
import org.pos.study.business.exceptions.EntityUnverifiable
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.resource.NoResourceFoundException
import javax.annotation.PostConstruct

@RestControllerAdvice
class ExceptionHandler {

    @PostConstruct
    fun init() {
        println("ExceptionHandler initialized");
    }

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

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleDataIntegrityViolation(ex: HttpClientErrorException): ResponseEntity<*> {
        return ResponseEntity.status(ex.statusCode)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleDataIntegrityViolation(ex: NoResourceFoundException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleValidationExceptions(ex: HttpRequestMethodNotSupportedException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleValidationExceptions(ex: MethodArgumentTypeMismatchException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleValidationExceptions(ex: HttpMessageNotReadableException): ResponseEntity<*> {
        val concreteProblem = ex.message?.split("problem:")

        val status = when (ex.cause) {
            is InvalidFormatException -> HttpStatus.UNPROCESSABLE_ENTITY
            else -> HttpStatus.UNPROCESSABLE_ENTITY
        }

        concreteProblem?.size?.takeIf { it > 1 }?.let {
            return ResponseEntity.status(status)
                .body(EntityModel.of(mapOf("message" to ex.message?.split("problem:")[1])))
        }

        return ResponseEntity.status(status)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }


    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidationExceptions(ex: HandlerMethodValidationException): ResponseEntity<*> {
        for (error in ex.allErrors) {
            if (error is FieldError) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(EntityModel.of(mapOf("message" to ("${error.field}: ${error.defaultMessage ?: "Invalid value"}"))))
            }
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("message" to "Unknown validation error")))
    }



    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EntityModel.of(mapOf("message" to "${ex.fieldError?.field} ${ex.fieldError?.defaultMessage}")))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(EntityModel.of(mapOf("message" to (ex.cause?.cause?.message ?: "Data integrity violation"))))
    }

    @ExceptionHandler(ResourceAccessException::class)
    fun handleDataIntegrityViolation(ex: ResourceAccessException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(EntityModel.of(mapOf("message" to ex.message)))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleLectureNotFound(ex: ResponseStatusException): ResponseEntity<*> {
        return ResponseEntity.status(ex.statusCode)
            .body(EntityModel.of(mapOf("message" to ex.reason)))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<*> {
        ex.printStackTrace()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(EntityModel.of(mapOf("message" to ex.stackTrace)))
    }
}
