package com.pedromfmachado.sword.catz.di

import android.content.Context
import androidx.room.Room
import com.pedromfmachado.sword.catz.catbreeds.data.di.DatabaseModule
import com.pedromfmachado.sword.catz.catbreeds.data.local.CatBreedsDatabase
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideInMemoryDatabase(
        @ApplicationContext context: Context,
    ): CatBreedsDatabase =
        Room.inMemoryDatabaseBuilder(
            context,
            CatBreedsDatabase::class.java,
        ).allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun provideBreedDao(database: CatBreedsDatabase): BreedDao = database.breedDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: CatBreedsDatabase): FavoriteDao = database.favoriteDao()
}
