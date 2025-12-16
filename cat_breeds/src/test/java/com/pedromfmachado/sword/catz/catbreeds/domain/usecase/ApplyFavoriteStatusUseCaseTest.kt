package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.test.aBreed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplyFavoriteStatusUseCaseTest {
    private val useCase = ApplyFavoriteStatusUseCase()

    @Test
    fun `sets isFavorite true for breeds in favoriteIds`() {
        val breeds = listOf(aBreed(id = "1"), aBreed(id = "2"), aBreed(id = "3"))
        val favoriteIds = setOf("1", "3")

        val result = useCase(breeds, favoriteIds)

        assertTrue(result[0].isFavorite)
        assertFalse(result[1].isFavorite)
        assertTrue(result[2].isFavorite)
    }

    @Test
    fun `sets isFavorite false for breeds not in favoriteIds`() {
        val breeds = listOf(aBreed(id = "1", isFavorite = true))
        val favoriteIds = emptySet<String>()

        val result = useCase(breeds, favoriteIds)

        assertFalse(result[0].isFavorite)
    }

    @Test
    fun `preserves other breed properties`() {
        val breed = aBreed(id = "1", name = "Persian", origin = "Iran")
        val result = useCase(listOf(breed), setOf("1"))

        assertEquals(breed.id, result[0].id)
        assertEquals(breed.name, result[0].name)
        assertEquals(breed.origin, result[0].origin)
    }

    @Test
    fun `returns empty list for empty input`() {
        val result = useCase(emptyList(), setOf("1"))
        assertTrue(result.isEmpty())
    }
}
