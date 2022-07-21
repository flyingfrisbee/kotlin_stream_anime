package com.example.streamanime.data.repository

import com.example.streamanime.data.remote.AnimeServices
import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.data.remote.dto.response.*
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject

class AnimeServicesRepositoryImpl @Inject constructor(
    private val api: AnimeServices
) : AnimeServicesRepository {
    override suspend fun getRecentAnimes(page: Int): Resource<List<RecentAnimeResponse>> {
        return try {
            val response = api.getRecentAnimes(page)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body.data)
            } else {
                val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                Resource.Error(errorResult.message)
            }
        } catch (e: Exception) {
            Resource.Error("No internet connection")
        }
    }

    override suspend fun getSearchTitleResults(keyword: String): Resource<List<SearchTitleResponse>> {
        return try {
            val response = api.getSearchTitleResults(keyword)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body.data)
            } else {
                val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                Resource.Error(errorResult.message)
            }
        } catch (e: Exception) {
            Resource.Error("No internet connection")
        }
    }

    override suspend fun getAnimeDetail(
        id: String,
        request: AnimeDetailRequest
    ): Resource<AnimeDetailResponse> {
        return try {
            val response = api.getAnimeDetail(id, request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body.data)
            } else {
                val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                Resource.Error(errorResult.message)
            }
        } catch (e: Exception) {
            Resource.Error("No internet connection")
        }
    }

    override suspend fun getVideoUrl(endpoint: String): Resource<VideoUrlResponse> {
        return try {
            val response = api.getVideoUrl(endpoint)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body.data)
            } else {
                val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                Resource.Error(errorResult.message)
            }
        } catch (e: Exception) {
            Resource.Error("No internet connection")
        }
    }

    override suspend fun sendUserToken(request: UserTokenRequest): Resource<Any?> {
        return try {
            val response = api.sendUserToken(request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body.data)
            } else {
                val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                Resource.Error(errorResult.message)
            }
        } catch (e: Exception) {
            Resource.Error("No internet connection")
        }
    }
}