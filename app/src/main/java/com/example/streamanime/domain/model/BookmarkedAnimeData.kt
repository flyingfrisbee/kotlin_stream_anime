package com.example.streamanime.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["internal_id"], unique = true)])
data class BookmarkedAnimeData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "internal_id")
    val internalId: String,
    val title: String,
    val imageUrl: String,
    val latestEpisode: String,
    val timestamp: Long,
    val haveNewUpdate: Boolean = false,
)
