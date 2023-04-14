package com.example.streamanime.data.remote

import com.example.streamanime.core.utils.Constants
import com.example.streamanime.data.remote.dto.request.*
import com.example.streamanime.data.remote.dto.response.*
import com.example.streamanime.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface AnimeServices {
    @POST(Constants.SECRET_ROUTE)
    suspend fun registerUser(
        @Body req: UserTokenRequest
    ): Response<GenericResponse<UserTokenData>>

    @GET
    suspend fun refreshAccessToken(
        @Header("Authorization") refreshToken: String,
        @Url rawURL: String,
    ): Response<GenericResponse<RefreshTokenData>>

    @GET("/api/v1/anime/recent")
    suspend fun getRecentAnimes(
        @Query("page") page: Int
    ): Response<GenericResponse<List<RecentAnimeData>>>

    @GET("/api/v1/anime/search")
    suspend fun getSearchTitleResults(
        @Query("keywords") keywords: String
    ): Response<GenericResponse<List<SearchTitleData>>>

    @POST("/api/v1/anime/detail-alt")
    suspend fun getAnimeDetailAlt(
        @Body request: AnimeDetailAltRequest
    ): Response<GenericResponse<AnimeDetailData>>

    @GET("/api/v1/anime/detail/{animeID}")
    suspend fun getAnimeDetail(
        @Path("animeID") animeID: Int
    ): Response<GenericResponse<AnimeDetailData>>

    @POST("/api/v1/anime/video-url")
    suspend fun getVideoUrl(
        @Body request: VideoURLRequest
    ): Response<GenericResponse<VideoUrlData>>

    @POST("/api/v1/bookmark")
    suspend fun addToBookmark(
        @Body request: AddBookmarkRequest
    ): Response<GenericResponse<Any?>>

    @POST("/api/v1/bookmark/delete")
    suspend fun deleteBookmark(
        @Body request: DeleteBookmarkRequest
    ): Response<GenericResponse<Any?>>

    @POST("/api/v1/anime/sync")
    suspend fun syncBookmarkRequest(
        @Body request: SyncBookmarkRequest
    ): Response<GenericResponse<List<SyncBookmarkData>>>
}