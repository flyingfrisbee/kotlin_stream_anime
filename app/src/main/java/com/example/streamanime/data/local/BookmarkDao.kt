package com.example.streamanime.data.local

import androidx.room.*
import com.example.streamanime.domain.model.BookmarkedAnimeData

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkAnime(data: BookmarkedAnimeData)

    @Query(
        "DELETE FROM BookmarkedAnimeData WHERE id = :id"
    )
    suspend fun deleteBookmarkedAnime(id: Int)

    @Query(
        "SELECT * FROM BookmarkedAnimeData " +
        "ORDER BY haveNewUpdate DESC, updatedAtTimestamp DESC"
    )
    suspend fun getBookmarkedAnimes(): List<BookmarkedAnimeData>

    @Query(
        "UPDATE BookmarkedAnimeData " +
        "SET haveNewUpdate = 0 ," +
        "latestEpisodeLocal = latestEpisodeRemote " +
        "WHERE id = :id"
    )
    suspend fun updateField(id: Int)

    @Query(
        "UPDATE BookmarkedAnimeData " +
        "SET latestEpisodeRemote = :latestEpisode , " +
        "updatedAtTimestamp = :updatedAtTimestamp " +
        "WHERE id = :id"
    )
    suspend fun syncAnimeData(id: Int, latestEpisode: String, updatedAtTimestamp: Long)
}