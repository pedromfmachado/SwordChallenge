package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.ImageDto
import org.junit.Assert.assertEquals
import org.junit.Test

class BreedMapperTest {

    private val mapper = BreedMapper()

    @Test
    fun `mapToDomain maps all fields correctly`() {
        val dto = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifeSpan = "14 - 15",
            image = ImageDto(url = "https://example.com/cat.jpg")
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals("abys", breed.id)
        assertEquals("Abyssinian", breed.name)
        assertEquals("Egypt", breed.origin)
        assertEquals("Active, Energetic", breed.temperament)
        assertEquals("The Abyssinian is easy to care for", breed.description)
        assertEquals(14, breed.lifespanLow)
        assertEquals(15, breed.lifespanHigh)
        assertEquals("https://example.com/cat.jpg", breed.imageUrl)
        assertEquals(false, breed.isFavorite)
    }

    @Test
    fun `mapToDomain handles null image`() {
        val dto = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active",
            description = "Description",
            lifeSpan = "12 - 14",
            image = null
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals("", breed.imageUrl)
    }

    @Test
    fun `parseLifespan handles single number`() {
        val dto = BreedDto(
            id = "test",
            name = "Test",
            origin = "Test",
            temperament = "Test",
            description = "Test",
            lifeSpan = "15",
            image = null
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals(15, breed.lifespanLow)
        assertEquals(15, breed.lifespanHigh)
    }

    @Test
    fun `parseLifespan handles invalid format`() {
        val dto = BreedDto(
            id = "test",
            name = "Test",
            origin = "Test",
            temperament = "Test",
            description = "Test",
            lifeSpan = "unknown",
            image = null
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals(0, breed.lifespanLow)
        assertEquals(0, breed.lifespanHigh)
    }

    @Test
    fun `mapToDomain maps list correctly`() {
        val dtos = listOf(
            BreedDto("a", "A", "O", "T", "D", "10 - 12", null),
            BreedDto("b", "B", "O", "T", "D", "8 - 10", null)
        )

        val breeds = mapper.mapToDomain(dtos)

        assertEquals(2, breeds.size)
        assertEquals("a", breeds[0].id)
        assertEquals("b", breeds[1].id)
    }
}
