package com.example.streamanime.presentation.stream_activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamanime.core.utils.Constants.FCM_TOKEN
import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.data.remote.dto.request.CreateBookmarkRequest
import com.example.streamanime.domain.model.AnimeDetailData
import com.example.streamanime.domain.model.BookmarkedAnimeData
import com.example.streamanime.domain.model.enumerate.LoadingType
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.domain.repository.BookmarkServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    var id: String? = null
    var isInternalId = false

    private val _animeDetail = MutableLiveData<AnimeDetailData>()
    val animeDetail: LiveData<AnimeDetailData> = _animeDetail

    fun getAnimeDetail() = viewModelScope.launch {
        remoteRepo.getAnimeDetail(
            id!!,
            AnimeDetailRequest(isInternalId)
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
            remoteRepo.getVideoUrl(endpoint).collect { resource ->
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
            remoteRepo.createBookmark(
                CreateBookmarkRequest(
                    internalId = internalId,
                    latestEpisode = episodeList.last().episodeForUi,
                    userToken = sharedPref.getString(FCM_TOKEN, null) ?: ""
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.insertBookmarkAnime(
                            BookmarkedAnimeData(
                                internalId = internalId,
                                title = title,
                                imageUrl = imageUrl,
                                latestEpisode = latestEpisode,
                                timestamp = System.currentTimeMillis()
                            )
                        )

                        _successBookmark.value = "Success bookmark $title"
                        _successBookmark.value = ""
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
    val nonAds = mutableListOf(
        "jwpcdn",
        "movcloud",
        "stream",
        "exp=",
        "expiry=",
        ".m3u8",
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