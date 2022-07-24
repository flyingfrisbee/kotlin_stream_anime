package com.example.streamanime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.streamanime.domain.model.BookmarkedAnimeData

@Database(entities = [BookmarkedAnimeData::class], version = 1)
abstract class AnimeDatabase : RoomDatabase() {

    abstract val dao: BookmarkDao
}