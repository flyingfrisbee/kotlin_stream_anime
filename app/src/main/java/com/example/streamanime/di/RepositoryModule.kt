package com.example.streamanime.di

import com.example.streamanime.data.repository.AnimeServicesRepositoryImpl
import com.example.streamanime.data.repository.BookmarkServicesRepositoryImpl
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.domain.repository.BookmarkServicesRepository
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
    abstract fun bindAnimeServicesImpl(
        animeServicesRepositoryImpl: AnimeServicesRepositoryImpl
    ): AnimeServicesRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkServicesImpl(
        bookmarkServicesRepositoryImpl: BookmarkServicesRepositoryImpl
    ): BookmarkServicesRepository
}