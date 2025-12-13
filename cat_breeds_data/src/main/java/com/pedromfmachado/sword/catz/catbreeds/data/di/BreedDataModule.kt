package com.pedromfmachado.sword.catz.catbreeds.data.di

import com.pedromfmachado.sword.catz.catbreeds.api.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.data.repository.BreedRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BreedDataModule {

    @Binds
    @Singleton
    abstract fun bindBreedRepository(impl: BreedRepositoryImpl): BreedRepository
}
