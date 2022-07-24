package com.example.streamanime.presentation.main_activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamanime.core.fcm.FcmService
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.Constants.FCM_TOKEN
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.domain.model.RecentAnimeData
import com.example.streamanime.domain.model.SearchTitleData
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AnimeServicesRepository,
    private val sharedPref: SharedPreferences
) : ViewModel() {
    private var searchJob: Job? = null
    val fcmService = FcmService()

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

    private val _titleResults = MutableLiveData<List<SearchTitleData>>()
    val titleResults: LiveData<List<SearchTitleData>> = _titleResults

    private fun getTitleResults(keyword: String) = viewModelScope.launch {
        delay(1000L)
        repo.getSearchTitleResults(keyword).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _titleResults.value = resource.data!!
                }
                is Resource.Error -> {
                    _titleResults.value = emptyList()
                    insertErrorMessage(resource.msg!!)
                }
                else -> {}
            }
        }
    }

    fun changeTitleResultsValue(value: List<SearchTitleData>) = viewModelScope.launch {
        searchJob?.cancel()
        _titleResults.value = value
    }

    fun startSearchJob(keyword: String) {
        searchJob?.cancel()
        searchJob = getTitleResults(keyword)
    }

    fun sendTokenToServer() = fcmService {
        viewModelScope.launch {
            repo.sendUserToken(UserTokenRequest(it)).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        sharedPref.edit().putString(Constants.FCM_TOKEN, it).apply()
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> {}
                }
            }
        }
    }

    private val _recentAnimes = MutableLiveData<List<RecentAnimeData>>()
    val recentAnimes: LiveData<List<RecentAnimeData>> = _recentAnimes

    var page = 1

    fun getRecentAnimes() = viewModelScope.launch {
        repo.getRecentAnimes(page).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (page == 1) {
                        _recentAnimes.value = resource.data!!
                    } else {
                        recentAnimes.value?.let {
                            val listPlaceholder = it.toMutableList()
                            listPlaceholder.addAll(resource.data!!)

                            _recentAnimes.value = listPlaceholder
                        }
                    }
                    page++
                }
                is Resource.Error -> {
                    insertErrorMessage(resource.msg!!)
                }
                else -> { changeLoadingValue(true) }
            }
        }
        changeLoadingValue(false)
    }

    fun reloadRecentAnimes() = viewModelScope.launch {
        repo.getRecentAnimes(1).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    recentAnimes.value?.let {
                        val shouldReplaceList = resource.data!![0] != it[0]
                        if (shouldReplaceList) {
                            _recentAnimes.value = resource.data!!
                            page = 2
                        }
                    }
                }
                is Resource.Error -> {
                    insertErrorMessage(resource.msg!!)
                }
                else -> {}
            }
        }
        changeLoadingValue(false)
    }

    init {
        sendTokenToServer()
        getRecentAnimes()
    }
}