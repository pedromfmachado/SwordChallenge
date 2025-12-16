package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.test.aBreed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculateAverageLifespanUseCaseTest {
    private val useCase = CalculateAverageLifespanUseCase()

    @Test
    fun `returns null for empty list`() {
        val result = useCase(emptyList())
        assertNull(result)
    }

    @Test
    fun `calculates average lifespan for single breed`() {
        val breeds = listOf(aBreed(lifespanLow = 12, lifespanHigh = 18))
        val result = useCase(breeds)
        assertEquals(15, result)
    }

    @Test
    fun `calculates average lifespan for multiple breeds`() {
        val breeds = listOf(
            aBreed(id = "1", lifespanLow = 12, lifespanHigh = 18),
            aBreed(id = "2", lifespanLow = 10, lifespanHigh = 14),
            aBreed(id = "3", lifespanLow = 14, lifespanHigh = 20),
        )
        val result = useCase(breeds)
        assertEquals(14, result)
    }
}
