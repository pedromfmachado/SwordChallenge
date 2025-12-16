package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.test.aBreed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterBreedsByNameUseCaseTest {
    private val useCase = FilterBreedsByNameUseCase()

    private val breeds = listOf(
        aBreed(id = "1", name = "Persian"),
        aBreed(id = "2", name = "Maine Coon"),
        aBreed(id = "3", name = "Siamese"),
    )

    @Test
    fun `returns all breeds when query is blank`() {
        val result = useCase(breeds, "")
        assertEquals(breeds, result)
    }

    @Test
    fun `returns all breeds when query is whitespace`() {
        val result = useCase(breeds, "   ")
        assertEquals(breeds, result)
    }

    @Test
    fun `filters breeds by name case insensitively`() {
        val result = useCase(breeds, "persian")
        assertEquals(1, result.size)
        assertEquals("Persian", result[0].name)
    }

    @Test
    fun `filters breeds by partial name match`() {
        val result = useCase(breeds, "coon")
        assertEquals(1, result.size)
        assertEquals("Maine Coon", result[0].name)
    }

    @Test
    fun `returns empty list when no breeds match`() {
        val result = useCase(breeds, "Abyssinian")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns multiple matches when query matches multiple breeds`() {
        val breedsWithSimilarNames = listOf(
            aBreed(id = "1", name = "Persian"),
            aBreed(id = "2", name = "Himalayan Persian"),
        )
        val result = useCase(breedsWithSimilarNames, "Persian")
        assertEquals(2, result.size)
    }
}
