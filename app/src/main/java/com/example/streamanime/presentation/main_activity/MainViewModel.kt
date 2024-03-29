package com.example.streamanime.presentation.main_activity

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamanime.core.fcm.FcmService
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.Constants.ACCESS_TOKEN
import com.example.streamanime.core.utils.Constants.FCM_TOKEN
import com.example.streamanime.core.utils.Constants.REFRESH_TOKEN
import com.example.streamanime.data.remote.dto.request.AddBookmarkRequest
import com.example.streamanime.data.remote.dto.request.DeleteBookmarkRequest
import com.example.streamanime.data.remote.dto.request.SyncBookmarkRequest
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.domain.model.BookmarkedAnimeData
import com.example.streamanime.domain.model.RecentAnimeData
import com.example.streamanime.domain.model.SearchTitleData
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.domain.repository.BookmarkServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepo: AnimeServicesRepository,
    private val localRepo: BookmarkServicesRepository,
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

    private fun getTitleResults(keywords: String) = viewModelScope.launch {
        delay(1000L)
        remoteRepo.getSearchTitleResults(keywords).collect { resource ->
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

    private fun sendTokenToServer() = fcmService {
        val shouldRegisterUser = sharedPref.getString(REFRESH_TOKEN, null) == null
        if (shouldRegisterUser) {
            viewModelScope.launch {
                remoteRepo.registerUser(UserTokenRequest(it)).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            sharedPref.edit().apply {
                                putString(FCM_TOKEN, it)
                                resource.data?.let { data ->
                                    putString(ACCESS_TOKEN, data.accessToken)
                                    putString(REFRESH_TOKEN, data.refreshToken)
                                }
                            }.apply()
                            getRecentAnimes()
                        }
                        is Resource.Error -> {
                            insertErrorMessage(resource.msg!!)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private val _recentAnimes = MutableLiveData<List<RecentAnimeData>>()
    val recentAnimes: LiveData<List<RecentAnimeData>> = _recentAnimes

    var page = 1

    fun getRecentAnimes() = viewModelScope.launch {
        val hasAccessToken = sharedPref.getString(ACCESS_TOKEN, null)
        hasAccessToken?.let {
            remoteRepo.getRecentAnimes(page).collect { resource ->
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

                        val loadedAnimesIsNotEmpty = resource.data!!.isNotEmpty()
                        if (loadedAnimesIsNotEmpty) {
                            page++
                        }
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> { changeLoadingValue(true) }
                }
            }
            changeLoadingValue(false)
        }
    }

    fun reloadRecentAnimes() = viewModelScope.launch {
        remoteRepo.getRecentAnimes(1).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    recentAnimes.value?.let {
                        val shouldReplaceList = resource.data!![0] != it[0]
                        if (shouldReplaceList) {
                            _recentAnimes.value = resource.data!!
                            page = 2
                        }
                        return@collect
                    }

                    _recentAnimes.value = resource.data!!
                    page++
                }
                is Resource.Error -> {
                    insertErrorMessage(resource.msg!!)
                }
                else -> {}
            }
        }
        changeLoadingValue(false)
    }

    // bookmarked anime fragment
    private val _bookmarkLoading = MutableLiveData(false)
    val bookmarkLoading: LiveData<Boolean> = _bookmarkLoading

    private val _bookmarkedAnimes = MutableLiveData<List<BookmarkedAnimeData>>(emptyList())
    val bookmarkedAnimes: LiveData<List<BookmarkedAnimeData>> = _bookmarkedAnimes

    fun getBookmarkedAnime() = viewModelScope.launch {
        _bookmarkedAnimes.value = localRepo.getBookmarkedAnimes()
    }

    private fun getUpdatedBookmarkedAnimes() = viewModelScope.launch {
        getBookmarkedAnime()

        val animeIDs = localRepo.getBookmarkedAnimes().map { it.id }
        if (animeIDs.isEmpty()) {
            return@launch
        }
        remoteRepo.syncBookmarkRequest(
            SyncBookmarkRequest(animeIDs)
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (resource.data!!.isNotEmpty()) {
                        resource.data.forEach {
                            localRepo.syncAnimeData(it.id, it.latestEpisode, it.updatedAtTimestamp)
                        }
                    }
                    getBookmarkedAnime()
                }
                is Resource.Error -> {
                    insertErrorMessage(resource.msg!!)
                }
                else -> {}
            }
        }
    }

    fun insertToBookmark(data: BookmarkedAnimeData) = viewModelScope.launch {
        sharedPref.getString(FCM_TOKEN, null)?.let {
            remoteRepo.addToBookmark(
                AddBookmarkRequest(
                    animeId = data.id,
                    latestEpisode = data.latestEpisodeLocal,
                    userToken = it,
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.insertBookmarkAnime(data)
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> {
                        _bookmarkLoading.value = true
                    }
                }
            }
            delay(500L)
            _bookmarkLoading.value = false
        }
    }

    fun deleteBookmarkedAnime(data: BookmarkedAnimeData, onSuccess: () -> Unit) = viewModelScope.launch {
        sharedPref.getString(FCM_TOKEN, null)?.let {
            remoteRepo.deleteBookmark(
                DeleteBookmarkRequest(
                    animeId = data.id,
                    userToken = it
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.deleteBookmarkedAnime(data.id)
                        onSuccess()
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> {
                        _bookmarkLoading.value = true
                    }
                }
            }
            delay(500L)
            _bookmarkLoading.value = false
        }
    }

    fun updateField(id: Int, latestEpisode: String, onSuccess: () -> Unit) = viewModelScope.launch {
        _bookmarkLoading.value = true

        bookmarkedAnimes.value?.let { animes ->
            val anime = animes.find { it.id == id }
            anime?.let {
                localRepo.updateField(id, latestEpisode)
                onSuccess()
            }
        }

        _bookmarkLoading.value = false
    }

    private val background = ColorDrawable(Color.parseColor("#80D5D5D5"))
    private var eventPosition: Int = -1

    fun getBackground(): ColorDrawable {
        return background
    }

    fun updatePosition(pos: Int) {
        eventPosition = pos
    }

    fun getPosition(): Int {
        return eventPosition
    }

    init {
        getRecentAnimes()
        sendTokenToServer()
        getUpdatedBookmarkedAnimes()
    }
}