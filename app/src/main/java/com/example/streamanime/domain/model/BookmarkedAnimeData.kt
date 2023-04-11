package com.example.streamanime.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class BookmarkedAnimeData(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val imageUrl: String,
    val latestEpisode: String,
    val timestamp: Long,
    val haveNewUpdate: Boolean = false,
)
