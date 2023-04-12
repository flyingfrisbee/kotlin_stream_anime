package com.example.streamanime.di

import android.content.SharedPreferences
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.data.remote.AnimeServices
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

var shouldUseBaseURL = true

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
            val url = if (shouldUseBaseURL) {
                "${Constants.BASE_URL}/api/v1/token/refresh"
            } else {
                "${Constants.SECOND_BASE_URL}/api/v1/token/refresh"
            }

            val resource = retrofit.refreshAccessToken("Bearer $refreshToken", url)
            resource.body()?.let {
                token = it.data.authToken
                sharedPref.edit().putString(Constants.ACCESS_TOKEN, token).apply()
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
            shouldUseBaseURL = !shouldUseBaseURL
            response.close()
            response = sendRequest(chain, sharedPref)
        }
        response
    }
}

fun sendRequest(chain: Interceptor.Chain, sharedPref: SharedPreferences): Response {
    var request: Request = chain.request()
    val accessToken = sharedPref.getString(Constants.ACCESS_TOKEN, null)
    accessToken?.let {
        request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + it)
            .build()
    }
    if (!shouldUseBaseURL) {
        request = request.newBuilder()
            .url(request.url.toString().replace(Constants.BASE_URL, Constants.SECOND_BASE_URL))
            .build()
    }
    return chain.proceed(request)
}

