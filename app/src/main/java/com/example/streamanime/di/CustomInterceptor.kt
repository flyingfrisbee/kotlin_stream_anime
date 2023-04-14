package com.example.streamanime.di

import android.content.SharedPreferences
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.data.remote.AnimeServices
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Inject

class Auth @Inject constructor(
    private val sharedPref: SharedPreferences
): Authenticator {
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL) // Will be ignored
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnimeServices::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        var token = ""
        val refreshToken = sharedPref.getString(Constants.REFRESH_TOKEN, null)!!

        runBlocking {
            var url = "${Constants.BASE_URL}/api/v1/token/refresh"
            try {
                val resource = retrofit.refreshAccessToken("Bearer $refreshToken", url)
                resource.body()?.let {
                    token = it.data.authToken
                    sharedPref.edit().putString(Constants.ACCESS_TOKEN, token).apply()
                }
            } catch (e: Exception) {
                url = url.replace(Constants.BASE_URL, Constants.SECOND_BASE_URL)
                val resource = retrofit.refreshAccessToken("Bearer $refreshToken", url)
                resource.body()?.let {
                    token = it.data.authToken
                    sharedPref.edit().putString(Constants.ACCESS_TOKEN, token).apply()
                }
            }

            if (token.isEmpty()) {
                url = if (url.contains(Constants.BASE_URL)) {
                    url.replace(Constants.BASE_URL, Constants.SECOND_BASE_URL)
                } else {
                    url.replace(Constants.SECOND_BASE_URL, Constants.BASE_URL)
                }
                val resource = retrofit.refreshAccessToken("Bearer $refreshToken", url)
                resource.body()?.let {
                    token = it.data.authToken
                    sharedPref.edit().putString(Constants.ACCESS_TOKEN, token).apply()
                }
            }
        }

        return response.request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $token")
            .build()
    }
}

fun handleRedirectInterceptor(sharedPref: SharedPreferences): Interceptor {
    return Interceptor { chain ->
        var response = sendRequest(chain, sharedPref)

        if (response.code != 200 && response.code != 401) {
            response.close()
            response = sendRequest(chain, sharedPref, true)
        }
        response
    }
}

fun sendRequest(chain: Interceptor.Chain, sharedPref: SharedPreferences, changeURL: Boolean = false): Response {
    var request: Request = chain.request()
    val accessToken = sharedPref.getString(Constants.ACCESS_TOKEN, null)
    accessToken?.let {
        request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + it)
            .build()
    }
    if (changeURL) {
        var url = request.url.toString()
        if (url.contains(Constants.BASE_URL)) {
            url = url.replace(Constants.BASE_URL, Constants.SECOND_BASE_URL)
        } else {
            url = url.replace(Constants.SECOND_BASE_URL, Constants.BASE_URL)
        }
        request = request.newBuilder()
            .url(url)
            .build()
    }
    return chain.proceed(request)
}

