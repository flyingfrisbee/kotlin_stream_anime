package com.example.streamanime.domain.repository

import androidx.room.Delete
import androidx.room.Query
import com.example.streamanime.domain.model.BookmarkedAnimeData

interface BookmarkServicesRepository {

    suspend fun insertBookmarkAnime(data: BookmarkedAnimeData)

    suspend fun deleteBookmarkedAnime(data: BookmarkedAnimeData)

    suspend fun getBookmarkedAnimes(): List<BookmarkedAnimeData>

    suspend fun updateField(internalId: String)
}