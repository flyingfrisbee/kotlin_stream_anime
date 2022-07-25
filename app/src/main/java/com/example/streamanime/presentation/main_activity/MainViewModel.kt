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
import com.example.streamanime.core.utils.Constants.FCM_TOKEN
import com.example.streamanime.data.remote.dto.request.CreateBookmarkRequest
import com.example.streamanime.data.remote.dto.request.DeleteBookmarkRequest
import com.example.streamanime.data.remote.dto.request.UserTokenRequest
import com.example.streamanime.domain.model.BookmarkedAnimeData
import com.example.streamanime.domain.model.RecentAnimeData
import com.example.streamanime.domain.model.SearchTitleData
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.example.streamanime.domain.repository.BookmarkServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

    private fun getTitleResults(keyword: String) = viewModelScope.launch {
        delay(1000L)
        remoteRepo.getSearchTitleResults(keyword).collect { resource ->
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
            remoteRepo.sendUserToken(UserTokenRequest(it)).collect { resource ->
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
        sharedPref.getString(FCM_TOKEN, null)?.let {
            remoteRepo.bookmarkedAnimeWithUpdate(it).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data!!.isNotEmpty()) {
                            resource.data!!.forEach {
                                localRepo.insertBookmarkAnime(it)
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
            delay(500L)
            _bookmarkLoading.value = false
        }
    }

    fun insertToBookmark(data: BookmarkedAnimeData) = viewModelScope.launch {
        sharedPref.getString(FCM_TOKEN, null)?.let {
            remoteRepo.createBookmark(
                CreateBookmarkRequest(
                    internalId = data.internalId,
                    latestEpisode = data.latestEpisode,
                    userToken = it
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.insertBookmarkAnime(data)
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> { _bookmarkLoading.value = true }
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
                    internalId = data.internalId,
                    userToken = it
                )
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        localRepo.deleteBookmarkedAnime(data)
                        onSuccess()
                    }
                    is Resource.Error -> {
                        insertErrorMessage(resource.msg!!)
                    }
                    else -> { _bookmarkLoading.value = true }
                }
            }
            delay(500L)
            _bookmarkLoading.value = false
        }
    }

    fun updateField(internalId: String) = viewModelScope.launch {
        localRepo.updateField(internalId)
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
        sendTokenToServer()
        getRecentAnimes()
        getUpdatedBookmarkedAnimes()
    }
}