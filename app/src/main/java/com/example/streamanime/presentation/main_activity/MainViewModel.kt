package com.example.streamanime.presentation.main_activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamanime.core.fcm.FcmService
import com.example.streamanime.core.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    val fcmService = FcmService()

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> =_errorMessage

    fun insertErrorMessage(text: String) = viewModelScope.launch {
        _errorMessage.value = text
        _errorMessage.value = ""
    }

    fun sendTokenToServer() = viewModelScope.launch {
        val shouldSendTokenToServer = fcmService(
            sharedPref.getString(Constants.FCM_TOKEN, null)
        ) {
            insertErrorMessage(it)
        }

        if (shouldSendTokenToServer) {
            // call API
        }
    }
}