package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculateAverageLifespanUseCaseTest {
    private val useCase = CalculateAverageLifespanUseCase()

    private fun createBreed(lifespanLow: Int, lifespanHigh: Int) = Breed(
        id = "1",
        name = "Test",
        imageUrl = "https://example.com/1.jpg",
        origin = "Test",
        temperament = "Test",
        description = "Test",
        lifespanLow = lifespanLow,
        lifespanHigh = lifespanHigh,
    )

    @Test
    fun `invoke returns null for empty list`() {
        val result = useCase(emptyList())
        assertNull(result)
    }

    @Test
    fun `invoke calculates average lifespan for single breed`() {
        val breeds = listOf(createBreed(12, 18))
        val result = useCase(breeds)
        // Average of (12 + 18) / 2 = 15
        assertEquals(15, result)
    }

    @Test
    fun `invoke calculates average lifespan for multiple breeds`() {
        // midpoint: 15, 12, 17 respectively
        val breeds = listOf(
            createBreed(12, 18),
            createBreed(10, 14),
            createBreed(14, 20),
        )
        val result = useCase(breeds)
        // Average of 15, 12, 17 = 44/3 = 14.67 -> 14 (truncated)
        assertEquals(14, result)
    }

    @Test
    fun `invoke truncates decimal in final result`() {
        // midpoint: 11, 13 respectively
        val breeds = listOf(
            createBreed(10, 12),
            createBreed(12, 14),
        )
        val result = useCase(breeds)
        // Average of 11, 13 = 12
        assertEquals(12, result)
    }
}
