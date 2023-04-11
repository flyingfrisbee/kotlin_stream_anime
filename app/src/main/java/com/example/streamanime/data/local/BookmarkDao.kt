package com.example.streamanime.data.local

import androidx.room.*
import com.example.streamanime.domain.model.BookmarkedAnimeData

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkAnime(data: BookmarkedAnimeData)

    @Delete
    suspend fun deleteBookmarkedAnime(data: BookmarkedAnimeData)

    @Query(
        "SELECT * FROM BookmarkedAnimeData " +
        "ORDER BY haveNewUpdate DESC, timestamp DESC"
    )
    suspend fun getBookmarkedAnimes(): List<BookmarkedAnimeData>

    @Query(
        "UPDATE BookmarkedAnimeData " +
        "SET haveNewUpdate = :newUpdate " +
        "WHERE id = :id"
    )
    suspend fun updateField(id: Int, newUpdate: Boolean = false)
}