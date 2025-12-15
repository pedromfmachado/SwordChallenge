package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.ImageDto
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class BreedMapperTest {
    private val mapper = BreedRemoteMapper()

    enum class MappingTestCase(
        val dto: BreedDto,
        val model: Breed,
    ) {
        AllFields(BASE_DTO, BASE_MODEL),
        NoImage(BASE_DTO.copy(image = null), BASE_MODEL.copy(imageUrl = "")),
        SingleLifespan(
            BASE_DTO.copy(lifeSpan = "20"),
            BASE_MODEL.copy(lifespanLow = 20, lifespanHigh = 20),
        ),
        InvalidLifespan(
            BASE_DTO.copy(lifeSpan = "unknown"),
            BASE_MODEL.copy(lifespanLow = 0, lifespanHigh = 0),
        ),
        EmptyLifespan(
            BASE_DTO.copy(lifeSpan = ""),
            BASE_MODEL.copy(lifespanLow = 0, lifespanHigh = 0),
        ),
    }

    @Test
    fun `mapToDomain maps all fields correctly`(
        @TestParameter testCase: MappingTestCase,
    ) {
        val breed = mapper.mapToDomain(testCase.dto)

        assertEquals(testCase.model, breed)
    }

    @Test
    fun `mapToDomain maps list correctly`() {
        val dto1 = BASE_DTO.copy(id = "a", name = "A")
        val dto2 = BASE_DTO.copy(id = "b", name = "B")

        val breeds = mapper.mapToDomain(listOf(dto1, dto2))

        assertEquals(2, breeds.size)
        assertEquals(BASE_MODEL.copy(id = "a", name = "A"), breeds[0])
        assertEquals(BASE_MODEL.copy(id = "b", name = "B"), breeds[1])
    }

    companion object {
        private val BASE_DTO = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifeSpan = "14 - 15",
            image = ImageDto(url = "https://example.com/cat.jpg"),
        )

        private val BASE_MODEL = Breed(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifespanLow = 14,
            lifespanHigh = 15,
            imageUrl = "https://example.com/cat.jpg",
            isFavorite = false,
        )
    }
}
