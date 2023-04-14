package com.example.streamanime.domain.repository

import com.example.streamanime.data.remote.dto.request.*
import com.example.streamanime.data.remote.dto.response.*
import com.example.streamanime.domain.model.*
import com.example.streamanime.domain.model.enumerate.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.*

interface AnimeServicesRepository {

    suspend fun registerUser(
        req: UserTokenRequest
    ): Flow<Resource<UserTokenData>>

    suspend fun getRecentAnimes(
        page: Int
    ): Flow<Resource<List<RecentAnimeData>>>

    suspend fun getSearchTitleResults(
        keywords: String
    ): Flow<Resource<List<SearchTitleData>>>

    suspend fun getAnimeDetailAlt(
        request: AnimeDetailAltRequest
    ): Flow<Resource<AnimeDetailData>>

    suspend fun getAnimeDetail(
        animeID: Int
    ): Flow<Resource<AnimeDetailData>>

    suspend fun getVideoUrl(
        request: VideoURLRequest
    ): Flow<Resource<VideoUrlData>>

    suspend fun addToBookmark(
        request: AddBookmarkRequest
    ): Flow<Resource<Any?>>

    suspend fun deleteBookmark(
        request: DeleteBookmarkRequest
    ): Flow<Resource<Any?>>

    suspend fun syncBookmarkRequest(
        request: SyncBookmarkRequest
    ): Flow<Resource<List<SyncBookmarkData>>>

//    suspend fun createBookmark(
//        request: CreateBookmarkRequest
//    ): Flow<Resource<Any?>>
//
//    suspend fun deleteBookmark(
//        request: DeleteBookmarkRequest
//    ): Flow<Resource<Any?>>
//
//    suspend fun bookmarkedAnimeWithUpdate(
//        @Query("token") userToken: String
//    ): Flow<Resource<List<BookmarkedAnimeData>>>
//
//    suspend fun updateBookmarkedAnimeLatestEpisode(
//        request: CreateBookmarkRequest
//    ): Flow<Resource<Any?>>
}