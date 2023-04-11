package com.example.streamanime.data.repository

import com.example.streamanime.data.remote.AnimeServices
import com.example.streamanime.data.remote.dto.request.*
import com.example.streamanime.data.remote.dto.response.*
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.domain.model.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AnimeServicesRepositoryImpl @Inject constructor(
    private val api: AnimeServices
) : AnimeServicesRepository {
    override suspend fun registerUser(req: UserTokenRequest): Flow<Resource<UserTokenData>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.registerUser(req)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

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
                    for (anime in body.data) {
                        anime.getTimestamp()
                    }
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

    override suspend fun getSearchTitleResults(keywords: String): Flow<Resource<List<SearchTitleData>>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getSearchTitleResults(keywords)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

    override suspend fun getAnimeDetailAlt(
        request: AnimeDetailAltRequest
    ): Flow<Resource<AnimeDetailData>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getAnimeDetailAlt(request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

    override suspend fun getAnimeDetail(
        animeID: Int
    ): Flow<Resource<AnimeDetailData>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getAnimeDetail(animeID)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    body.data.getTimestamp()
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

    override suspend fun getVideoUrl(request: VideoURLRequest): Flow<Resource<VideoUrlData>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.getVideoUrl(request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
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
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
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
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

    override suspend fun bookmarkedAnimeWithUpdate(userToken: String): Flow<Resource<List<BookmarkedAnimeData>>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.bookmarkedAnimeWithUpdate(userToken)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }

    override suspend fun updateBookmarkedAnimeLatestEpisode(request: CreateBookmarkRequest): Flow<Resource<Any?>> {
        return flow {
            emit(Resource.Loading())

            val response = try {
                api.updateBookmarkedAnimeLatestEpisode(request)
            } catch (e: Exception) {
                emit(Resource.Error("No internet connection"))
                null
            }

            response?.let {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    emit(Resource.Success(body.data))
                } else {
                    try {
                        val errorResult = Gson().fromJson(response.errorBody()?.charStream(), GenericResponse::class.java)
                        emit(Resource.Error(errorResult.message))
                    } catch (e: Exception) {}
                }
            }
        }
    }
}