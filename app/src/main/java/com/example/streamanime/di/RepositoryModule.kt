package com.example.streamanime.di

import com.example.streamanime.data.repository.AnimeServicesRepositoryImpl
import com.example.streamanime.domain.repository.AnimeServicesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bind(
        animeServicesRepositoryImpl: AnimeServicesRepositoryImpl
    ): AnimeServicesRepository
}