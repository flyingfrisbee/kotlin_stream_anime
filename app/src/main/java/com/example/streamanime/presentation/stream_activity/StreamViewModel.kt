package com.example.streamanime.presentation.stream_activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamanime.core.utils.Constants.FCM_TOKEN
import com.example.streamanime.data.remote.dto.request.AddBookmarkRequest
import com.example.streamanime.data.remote.dto.request.AnimeDetailAltRequest
import com.example.streamanime.data.remote.dto.request.DeleteBookmarkRequest
import com.example.streamanime.data.remote.dto.request.VideoURLRequest
import com.example.streamanime.domain.model.AnimeDetailData
import com.example.streamanime.domain.model.BookmarkedAnimeData
import com.example.streamanime.domain.model.enumerate.LoadingType
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.domain.repository.BookmarkServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val remoteRepo: AnimeServicesRepository,
    private val localRepo: BookmarkServicesRepository,
    private val sharedPref: SharedPreferences
) : ViewModel() {
    private val _loadingType = MutableLiveData<LoadingType>()
    val loadingType: LiveData<LoadingType> = _loadingType

    fun changeLoadingValue(loadingType: LoadingType) = viewModelScope.launch {
        _loadingType.value = loadingType
    }

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun insertErrorMessage(text: String) = viewModelScope.launch {
        _errorMessage.value = text
        _errorMessage.value = ""
    }

    var detailAltTitle: String? = null
    var detailAltEndpoint: String? = null
    var animeID: Int = -1

    private val _animeIsBookmarked = MutableLiveData<Boolean>()
    val animeIsBookmarked: LiveData<Boolean> = _animeIsBookmarked

    fun setStatus(isBookmarked: Boolean) = viewModelScope.launch {
        _animeIsBookmarked.value = isBookmarked
    }

    private val _animeDetail = MutableLiveData<AnimeDetailData>()
    val animeDetail: LiveData<AnimeDetailData> = _animeDetail

    fun getAnimeDetail() = viewModelScope.launch {
        // For detail alt
        detailAltTitle?.let {
            remoteRepo.getAnimeDetailAlt(AnimeDetailAltRequest(
                endpoint = detailAltEndpoint!!,
                title = detailAltTitle!!,
            )).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _animeDetail.value = resource.data!!
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> { changeLoadingValue(LoadingType.LoadAnimeDetail(true)) }
                }
            }
            changeLoadingValue(LoadingType.LoadAnimeDetail(false))
            return@launch
        }
        // For detail from DB
        remoteRepo.getAnimeDetail(
            animeID
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _animeDetail.value = resource.data!!
                }
                is Resource.Error -> {
                    insertErrorMessage(resource.msg!!)
                }
                else -> { changeLoadingValue(LoadingType.LoadAnimeDetail(true)) }
            }
        }
        changeLoadingValue(LoadingType.LoadAnimeDetail(false))
    }

    var getVideoUrlJob: Job? = null

    private val _videoUrl = MutableLiveData("")
    val videoUrl: LiveData<String> = _videoUrl

    fun getVideoUrl(endpoint: String) {
        getVideoUrlJob?.cancel()
        getVideoUrlJob = viewModelScope.launch {
            delay(1000L)
            remoteRepo.getVideoUrl(
                VideoURLRequest(endpoint)
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _videoUrl.value = resource.data!!.videoUrl
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> {}
                }
            }
        }
    }

    private val _successBookmark = MutableLiveData<String>("")
    val successBookmark: LiveData<String> = _successBookmark

    fun bookmarkAnime() = viewModelScope.launch {
        animeDetail.value!!.apply {
            remoteRepo.addToBookmark(
                AddBookmarkRequest(
                    animeId = this.id,
                    latestEpisode = this.latestEpisode,
                    userToken = sharedPref.getString(FCM_TOKEN, null) ?: "",
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.insertBookmarkAnime(
                            BookmarkedAnimeData(
                                id = this.id,
                                title = this.title,
                                imageUrl = this.imageUrl,
                                latestEpisodeLocal = this.latestEpisode,
                                latestEpisodeRemote = this.latestEpisode,
                                updatedAtTimestamp = updatedAtTimestamp ?: 0,
                            )
                        )

                        _successBookmark.value = "Success bookmark $title"
                        _successBookmark.value = ""
                        _animeIsBookmarked.value = !animeIsBookmarked.value!!
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> { changeLoadingValue(LoadingType.BookmarkAnime(true)) }
                }
            }
            changeLoadingValue(LoadingType.BookmarkAnime(false))
        }
    }

    fun unbookmarkAnime() = viewModelScope.launch {
        animeDetail.value!!.apply {
            remoteRepo.deleteBookmark(
                DeleteBookmarkRequest(
                    animeId = this.id,
                    userToken = sharedPref.getString(FCM_TOKEN, null) ?: "",
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.deleteBookmarkedAnime(this.id)
                        _successBookmark.value = "Success removing $title from bookmarked list"
                        _animeIsBookmarked.value = !animeIsBookmarked.value!!
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> { changeLoadingValue(LoadingType.BookmarkAnime(true)) }
                }
            }
            changeLoadingValue(LoadingType.BookmarkAnime(false))
        }
    }

    var currentSelectedEpisodeIndex = -1
    var adsChecking = true
    var alreadyGotHostUrl = false
    var alreadyGotCdnURL = false

    val nonAds = mutableListOf(
        ".ts",
        ".mp4",
        ".m4s",
        ".m3u8",
        "jwpcdn",
        ".vtt",
    )
    val initialSize = nonAds.size

    fun isAdsURL(URL: String): Boolean {
        nonAds.forEach {
            if (URL.contains(it)) {
                return false
            }
        }
        return true
    }

    var turnOffAdsCheckingJob: Job? = null

    fun startTurnOffAdsCheckingJob() {
        adsChecking = true
        turnOffAdsCheckingJob?.cancel()
        turnOffAdsCheckingJob = viewModelScope.launch {
            delay(120000L)
            adsChecking = false
        }
    }
}