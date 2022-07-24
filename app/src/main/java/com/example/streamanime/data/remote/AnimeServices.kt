package com.example.streamanime.data.remote

import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.data.remote.dto.request.CreateBookmarkRequest
import com.example.streamanime.data.remote.dto.request.DeleteBookmarkRequest
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.data.remote.dto.response.*
import retrofit2.Response
import retrofit2.http.*

interface AnimeServices {

    @GET("/")
    suspend fun getRecentAnimes(
        @Query("page") page: Int
    ): Response<GenericResponse<List<RecentAnimeResponse>>>

    @GET("/title")
    suspend fun getSearchTitleResults(
        @Query("keyword") keyword: String
    ): Response<GenericResponse<List<SearchTitleResponse>>>

    @POST("/anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") id: String,
        @Body request: AnimeDetailRequest
    ): Response<GenericResponse<AnimeDetailResponse>>

    @GET("/video/{endpoint}")
    suspend fun getVideoUrl(
        @Path("endpoint") endpoint: String
    ): Response<GenericResponse<VideoUrlResponse>>

    @POST("/user/create")
    suspend fun sendUserToken(
        @Body request: UserTokenRequest
    ): Response<GenericResponse<Any?>>

    @POST("/bookmark/create")
    suspend fun createBookmark(
        @Body request: CreateBookmarkRequest
    ): Response<GenericResponse<Any?>>

    @POST("/bookmark/delete")
    suspend fun deleteBookmark(
        @Body request: DeleteBookmarkRequest
    ): Response<GenericResponse<Any?>>

    @GET("/bookmark/update")
    suspend fun bookmarkedAnimeWithUpdate(
        @Query("token") userToken: String
    ): Response<GenericResponse<List<BookmarkedAnimeResponse>>>
}