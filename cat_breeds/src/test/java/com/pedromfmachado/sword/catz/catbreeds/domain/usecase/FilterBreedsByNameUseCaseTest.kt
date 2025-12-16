package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterBreedsByNameUseCaseTest {
    private val useCase = FilterBreedsByNameUseCase()

    private fun createBreed(id: String, name: String) = Breed(
        id = id,
        name = name,
        imageUrl = "https://example.com/$id.jpg",
        origin = "Test",
        temperament = "Test",
        description = "Test",
        lifespanLow = 10,
        lifespanHigh = 15,
    )

    private val breeds = listOf(
        createBreed("1", "Persian"),
        createBreed("2", "Maine Coon"),
        createBreed("3", "Siamese"),
    )

    @Test
    fun `invoke returns all breeds when query is blank`() {
        val result = useCase(breeds, "")
        assertEquals(breeds, result)
    }

    @Test
    fun `invoke returns all breeds when query is whitespace`() {
        val result = useCase(breeds, "   ")
        assertEquals(breeds, result)
    }

    @Test
    fun `invoke filters breeds by name case insensitively`() {
        val result = useCase(breeds, "persian")
        assertEquals(1, result.size)
        assertEquals("Persian", result[0].name)
    }

    @Test
    fun `invoke filters breeds by partial name match`() {
        val result = useCase(breeds, "coon")
        assertEquals(1, result.size)
        assertEquals("Maine Coon", result[0].name)
    }

    @Test
    fun `invoke returns empty list when no breeds match`() {
        val result = useCase(breeds, "Abyssinian")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns multiple matches when query matches multiple breeds`() {
        val breedsWithSimilarNames = listOf(
            createBreed("1", "Persian"),
            createBreed("2", "Himalayan Persian"),
        )
        val result = useCase(breedsWithSimilarNames, "Persian")
        assertEquals(2, result.size)
    }
}
