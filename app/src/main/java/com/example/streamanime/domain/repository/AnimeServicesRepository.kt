package com.example.streamanime.domain.repository

import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.data.remote.dto.response.*
import com.example.streamanime.domain.model.enumerate.Resource
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeServicesRepository {

    suspend fun getRecentAnimes(
        page: Int
    ): Resource<List<RecentAnimeResponse>>

    suspend fun getSearchTitleResults(
        keyword: String
    ): Resource<List<SearchTitleResponse>>

    suspend fun getAnimeDetail(
        id: String,
        request: AnimeDetailRequest
    ): Resource<AnimeDetailResponse>

    suspend fun getVideoUrl(
        endpoint: String
    ): Resource<VideoUrlResponse>

    suspend fun sendUserToken(
        request: UserTokenRequest
    ): Resource<Any?>
}