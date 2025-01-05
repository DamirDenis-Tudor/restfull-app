package org.pos.study.presentation.aspects

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(2)
class ValidationAspect {

    @Before("@annotation(jakarta.validation.*)")
    fun checkValidationBeforeExecution(joinPoint: ProceedingJoinPoint) {
        joinPoint.proceed()
    }
}