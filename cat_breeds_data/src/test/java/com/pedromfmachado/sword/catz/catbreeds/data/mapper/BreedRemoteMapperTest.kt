package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.test.aBreedDto
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class BreedRemoteMapperTest {
    private val mapper = BreedRemoteMapper()

    enum class MappingTestCase(
        val dto: BreedDto,
        val model: Breed,
    ) {
        AllFields(
            dto = aBreedDto(),
            model = aBreed()
        ),
        NoImage(
            dto = aBreedDto(image = null),
            model = aBreed(imageUrl = "")
        ),
        SingleLifespan(
            dto = aBreedDto(lifeSpan = "20"),
            model = aBreed(lifespanLow = 20, lifespanHigh = 20),
        ),
        InvalidLifespan(
            dto = aBreedDto(lifeSpan = "unknown"),
            model = aBreed(lifespanLow = 0, lifespanHigh = 0),
        ),
        EmptyLifespan(
            dto = aBreedDto(lifeSpan = ""),
            model = aBreed(lifespanLow = 0, lifespanHigh = 0),
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
        val dtoList = listOf(aBreedDto(id = "a", name = "A"), aBreedDto(id = "b", name = "B"))
        val breedsList = listOf(aBreed(id = "a", name = "A"), aBreed(id = "b", name = "B"))

        val result = mapper.mapToDomain(dtoList)

        assertEquals(breedsList, result)
    }
}
