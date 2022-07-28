package com.example.streamanime.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.streamanime.R
import com.example.streamanime.core.alarm.ExactAlarm
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.data.local.AnimeDatabase
import com.example.streamanime.data.local.BookmarkDao
import com.example.streamanime.data.remote.AnimeServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val key = context.getString(R.string.shared_preference_instance_key)
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideExactAlarm(
        @ApplicationContext context: Context
    ): ExactAlarm {
        return ExactAlarm(context)
    }

    @Provides
    @Singleton
    fun provideAnimeApi(): AnimeServices {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnimeServices::class.java)
    }

    @Provides
    @Singleton
    fun provideAnimeDatabase(
        @ApplicationContext context: Context
    ): AnimeDatabase {
        return Room.databaseBuilder(
            context,
            AnimeDatabase::class.java,
            "anime_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(
        db: AnimeDatabase
    ): BookmarkDao {
        return db.dao
    }
}