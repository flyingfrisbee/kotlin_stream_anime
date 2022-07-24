package com.example.streamanime.data.repository

import com.example.streamanime.data.remote.AnimeServices
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
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.mappers.toData
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class AnimeServicesRepositoryImpl @Inject constructor(
    private val api: AnimeServices
) : AnimeServicesRepository {
    override suspend fun getRecentAnimes(page: Int): Flow<Resource<List<RecentAnimeData>>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getRecentAnimes(page)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data.map { it.toData() }))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }

    override suspend fun getSearchTitleResults(keyword: String): Flow<Resource<List<SearchTitleData>>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getSearchTitleResults(keyword)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data.map { it.toData() }))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }

    override suspend fun getAnimeDetail(
        id: String,
        request: AnimeDetailRequest
    ): Flow<Resource<AnimeDetailData>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getAnimeDetail(id, request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data.toData()))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }

    override suspend fun getVideoUrl(endpoint: String): Flow<Resource<VideoUrlData>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getVideoUrl(endpoint)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data.toData()))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }

    override suspend fun sendUserToken(request: UserTokenRequest): Flow<Resource<Any?>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.sendUserToken(request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }

    override suspend fun createBookmark(request: CreateBookmarkRequest): Flow<Resource<Any?>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.createBookmark(request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }

    override suspend fun deleteBookmark(request: DeleteBookmarkRequest): Flow<Resource<Any?>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.deleteBookmark(request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                    emit(Resource.Error(errorResult.message))
                }
            }
        }
    }
}