package org.pos.study.controllers.student

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class) // This will enable Mockito in the suite
class AllTests {
    @Test
    fun dummyTest() {
        // This is just to ensure the suite runs without any issues.
        assertTrue(true)
    }
}