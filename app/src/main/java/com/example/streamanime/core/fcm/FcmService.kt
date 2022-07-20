package com.example.streamanime.core.fcm

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class FcmService {

    operator fun invoke(cachedToken: String?, onError: (String) -> Unit): Boolean {
        if (cachedToken == null) {
            return true
        }

        val serverToken = getTokenFromServer(onError)
        if (serverToken.isBlank()) {
            return false
        }

        val shouldSendNewTokenToServer = !isTokenSame(cachedToken, serverToken)

        return shouldSendNewTokenToServer
    }

    private fun isTokenSame(cachedToken: String, serverToken: String): Boolean {
        return cachedToken == serverToken
    }

    private fun getTokenFromServer(onError: (String) -> Unit): String {
        var token = ""

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError("Failed retrieving token from firebase")
                return@OnCompleteListener
            }

            token = task.result
        })

        return token
    }
}