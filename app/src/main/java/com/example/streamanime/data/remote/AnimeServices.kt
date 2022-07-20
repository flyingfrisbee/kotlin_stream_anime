package com.example.streamanime.data.remote

import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.data.remote.dto.response.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeServices {

    @GET("/")
    suspend fun getRecentAnimes(
        @Query("page") page: Int
    ): Response<GenericResponse<List<RecentAnimeResponse>>>

    @GET("/title")
    suspend fun getSearchTitleResults(
        @Query("keyword") keyword: String
    ): Response<GenericResponse<List<SearchTitleResponse>>>

    @GET("/anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") id: String,
        @Body request: AnimeDetailRequest
    ): Response<GenericResponse<AnimeDetailResponse>>

    @GET("/video/{endpoint}")
    suspend fun getVideoUrl(
        @Path("endpoint") endpoint: String
    ): Response<GenericResponse<VideoUrlResponse>>
}