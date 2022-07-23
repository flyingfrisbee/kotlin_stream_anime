package com.example.streamanime.domain.repository

import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.data.remote.dto.request.CreateBookmarkRequest
import com.example.streamanime.data.remote.dto.request.DeleteBookmarkRequest
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.data.remote.dto.response.*
import com.example.streamanime.domain.model.AnimeDetailData
import com.example.streamanime.domain.model.RecentAnimeData
import com.example.streamanime.domain.model.SearchTitleData
import com.example.streamanime.domain.model.VideoUrlData
import com.example.streamanime.domain.model.enumerate.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.*

interface AnimeServicesRepository {

    suspend fun getRecentAnimes(
        page: Int
    ): Flow<Resource<List<RecentAnimeData>>>

    suspend fun getSearchTitleResults(
        keyword: String
    ): Flow<Resource<List<SearchTitleData>>>

    suspend fun getAnimeDetail(
        id: String,
        request: AnimeDetailRequest
    ): Flow<Resource<AnimeDetailData>>

    suspend fun getVideoUrl(
        endpoint: String
    ): Flow<Resource<VideoUrlData>>

    suspend fun sendUserToken(
        request: UserTokenRequest
    ): Flow<Resource<Any?>>

    suspend fun createBookmark(
        request: CreateBookmarkRequest
    ): Flow<Resource<Any?>>

    suspend fun deleteBookmark(
        request: DeleteBookmarkRequest
    ): Flow<Resource<Any?>>
}