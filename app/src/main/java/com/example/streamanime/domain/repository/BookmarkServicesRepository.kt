package com.example.streamanime.domain.repository

import androidx.room.Delete
import androidx.room.Query
import com.example.streamanime.domain.model.BookmarkedAnimeData

interface BookmarkServicesRepository {

    suspend fun insertBookmarkAnime(data: BookmarkedAnimeData)

    suspend fun deleteBookmarkedAnime(id: Int)

    suspend fun getBookmarkedAnimes(): List<BookmarkedAnimeData>

    suspend fun updateField(id: Int)

    suspend fun syncAnimeData(id: Int, latestEpisode: String, updatedAtTimestamp: Long)
}