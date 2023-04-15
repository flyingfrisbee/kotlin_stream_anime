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

    override suspend fun deleteBookmarkedAnime(id: Int) {
        dao.deleteBookmarkedAnime(id)
    }

    override suspend fun getBookmarkedAnimes(): List<BookmarkedAnimeData> {
        return dao.getBookmarkedAnimes()
    }

    override suspend fun updateField(id: Int, latestEpisode: String) {
        dao.updateField(id, latestEpisode)
    }

    override suspend fun syncAnimeData(id: Int, latestEpisode: String, updatedAtTimestamp: Long) {
        dao.syncAnimeData(id, latestEpisode, updatedAtTimestamp)
    }
}