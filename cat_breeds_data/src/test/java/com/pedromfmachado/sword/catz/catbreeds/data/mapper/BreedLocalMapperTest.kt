package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.BreedEntity
import com.pedromfmachado.sword.catz.catbreeds.data.test.aBreedEntity
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class BreedLocalMapperTest {
    private val mapper = BreedLocalMapper()

    @Test
    fun `mapToDomain maps all fields correctly with isFavorite always false`() {
        val breed = mapper.mapToDomain(aBreedEntity())

        assertEquals(aBreed(isFavorite = false), breed)
    }

    @Test
    fun `mapToEntity maps domain model to entity correctly`() {
        val result = mapper.mapToEntity(aBreed(isFavorite = false))

        assertEquals(aBreedEntity(), result)
    }

    @Test
    fun `mapToEntity maps domain model to entity correctly ignoring favorite status`() {
        val result = mapper.mapToEntity(aBreed(isFavorite = true))

        assertEquals(aBreedEntity(), result)
    }

    @Test
    fun `mapToDomain maps list correctly`() {
        val entities = listOf(aBreedEntity(id = "a", name = "A"), aBreedEntity(id = "b", name = "B"))
        val breeds = listOf(aBreed(id = "a", name = "A"), aBreed(id = "b", name = "B"))

        val result = mapper.mapToDomain(entities)

        assertEquals(breeds, result)
    }

    @Test
    fun `mapToDomain returns empty list for empty input`() {
        val result = mapper.mapToDomain(emptyList())
        assertEquals(emptyList<Breed>(), result)
    }

    @Test
    fun `mapToEntities maps list correctly`() {
        val breeds = listOf(aBreed(id = "a", name = "A"), aBreed(id = "b", name = "B"))
        val entities = listOf(aBreedEntity(id = "a", name = "A"), aBreedEntity(id = "b", name = "B"))

        val result = mapper.mapToEntities(breeds)

        assertEquals(entities, result)
    }

    @Test
    fun `mapToEntities returns empty list for empty input`() {
        val result = mapper.mapToEntities(emptyList())

        assertEquals(emptyList<BreedEntity>(), result)
    }
}
