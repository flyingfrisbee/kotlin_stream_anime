package com.example.streamanime.presentation.stream_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamanime.data.remote.dto.request.AnimeDetailRequest
import com.example.streamanime.domain.model.AnimeDetailData
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val repo: AnimeServicesRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun changeLoadingValue(loading: Boolean) = viewModelScope.launch {
        _isLoading.value = loading
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
        repo.getAnimeDetail(
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
                else -> { changeLoadingValue(true) }
            }
        }
        changeLoadingValue(false)
    }

    private val _videoUrl = MutableLiveData("")
    val videoUrl: LiveData<String> = _videoUrl

    fun getVideoUrl(endpoint: String) = viewModelScope.launch {
        repo.getVideoUrl(endpoint).collect { resource ->
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

//    var alreadyGotURL = false
//    var adsChecking = true
//
//    val listOfNotAds = mutableListOf(
//        "jwpcdn",
//        "movcloud",
//        "stream",
//        "exp=",
//        "expiry=",
//        ".m3u8",
//        // "peliscdn",
//        // "grafolio",
//        // "travis",
//        // "loadfast",
//
//    )
//
//    fun isAdsURL(URL: String): Boolean {
//        listOfNotAds.forEach {
//            if (URL.contains(it)) {
//                return false
//            }
//        }
//        return true
//    }
}