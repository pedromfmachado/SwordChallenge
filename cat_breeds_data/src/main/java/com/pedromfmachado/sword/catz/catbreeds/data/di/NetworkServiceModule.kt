package com.pedromfmachado.sword.catz.catbreeds.data.di

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {
    @Provides
    @Singleton
    fun provideCatApiService(retrofit: Retrofit): CatApiService = retrofit.create(CatApiService::class.java)
}
