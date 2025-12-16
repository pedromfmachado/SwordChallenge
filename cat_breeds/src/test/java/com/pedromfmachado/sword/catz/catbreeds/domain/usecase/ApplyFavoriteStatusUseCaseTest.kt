package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplyFavoriteStatusUseCaseTest {
    private val useCase = ApplyFavoriteStatusUseCase()

    private fun createBreed(id: String, isFavorite: Boolean = false) = Breed(
        id = id,
        name = "Breed $id",
        imageUrl = "https://example.com/$id.jpg",
        origin = "Test",
        temperament = "Test",
        description = "Test",
        lifespanLow = 10,
        lifespanHigh = 15,
        isFavorite = isFavorite,
    )

    @Test
    fun `invoke sets isFavorite true for breeds in favoriteIds`() {
        val breeds = listOf(createBreed("1"), createBreed("2"), createBreed("3"))
        val favoriteIds = setOf("1", "3")

        val result = useCase(breeds, favoriteIds)

        assertTrue(result[0].isFavorite)
        assertFalse(result[1].isFavorite)
        assertTrue(result[2].isFavorite)
    }

    @Test
    fun `invoke sets isFavorite false for breeds not in favoriteIds`() {
        val breeds = listOf(createBreed("1", isFavorite = true))
        val favoriteIds = emptySet<String>()

        val result = useCase(breeds, favoriteIds)

        assertFalse(result[0].isFavorite)
    }

    @Test
    fun `invoke preserves other breed properties`() {
        val breed = createBreed("1")
        val result = useCase(listOf(breed), setOf("1"))

        assertEquals(breed.id, result[0].id)
        assertEquals(breed.name, result[0].name)
        assertEquals(breed.origin, result[0].origin)
    }

    @Test
    fun `invoke returns empty list for empty input`() {
        val result = useCase(emptyList(), setOf("1"))
        assertTrue(result.isEmpty())
    }
}
