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
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AnimeServicesRepository,
    private val sharedPref: SharedPreferences
) : ViewModel() {
    val fcmService = FcmService()

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> =_errorMessage

    fun insertErrorMessage(text: String) = viewModelScope.launch {
        _errorMessage.value = text
        _errorMessage.value = ""
    }

    fun sendTokenToServer() = fcmService {
        viewModelScope.launch {
            val resource = repo.sendUserToken(UserTokenRequest(it))
            if (resource is Resource.Success) {
                sharedPref.edit().putString(Constants.FCM_TOKEN, it).apply()
            } else {
                insertErrorMessage(resource.msg!!)
            }
        }

    }

    init {
        sendTokenToServer()
    }
}