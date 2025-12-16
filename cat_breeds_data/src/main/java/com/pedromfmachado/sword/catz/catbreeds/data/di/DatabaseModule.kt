package com.pedromfmachado.sword.catz.catbreeds.data.di

import android.content.Context
import androidx.room.Room
import com.pedromfmachado.sword.catz.catbreeds.data.local.CatBreedsDatabase
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideCatBreedsDatabase(
        @ApplicationContext context: Context,
    ): CatBreedsDatabase =
        Room.databaseBuilder(
            context,
            CatBreedsDatabase::class.java,
            "cat_breeds_database",
        ).build()

    @Provides
    @Singleton
    fun provideBreedDao(database: CatBreedsDatabase): BreedDao = database.breedDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: CatBreedsDatabase): FavoriteDao = database.favoriteDao()
}
