package com.example.streamanime.data.repository

import com.example.streamanime.data.local.BookmarkDao
import com.example.streamanime.domain.model.BookmarkedAnimeData
import com.example.streamanime.domain.repository.BookmarkServicesRepository
import javax.inject.Inject

class BookmarkServicesRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao
) : BookmarkServicesRepository {
    override suspend fun insertBookmarkAnime(data: BookmarkedAnimeData) {
        dao.insertBookmarkAnime(data)
    }

    override suspend fun deleteBookmarkedAnime(data: BookmarkedAnimeData) {
        dao.deleteBookmarkedAnime(data)
    }

    override suspend fun getBookmarkedAnimes(): List<BookmarkedAnimeData> {
        return dao.getBookmarkedAnimes()
    }

    override suspend fun updateField(internalId: String) {
        dao.updateField(internalId)
    }
}