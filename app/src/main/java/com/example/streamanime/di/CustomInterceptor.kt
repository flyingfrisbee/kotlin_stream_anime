package com.example.streamanime.di

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.data.remote.AnimeServices
import com.example.streamanime.domain.model.enumerate.Resource
import com.example.streamanime.domain.repository.AnimeServicesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import javax.inject.Inject

class Auth @Inject constructor(
    private val sharedPref: SharedPreferences
): Authenticator {
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnimeServices::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        var token = ""
        val refreshToken = sharedPref.getString(Constants.REFRESH_TOKEN, null)!!

        runBlocking {
            val resource = retrofit.refreshAccessToken("Bearer $refreshToken")
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
        var request: Request = chain.request()
        sharedPref.getString(Constants.ACCESS_TOKEN, null)?.let {
            request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + it)
                .build()
        }
        var response: Response = chain.proceed(request)
        if (response.code != 200 && response.code != 401 && !request.url.toString().contains(Constants.SECOND_BASE_URL)) {
            response.close()
            request = request.newBuilder()
                .url(request.url.toString().replace(Constants.BASE_URL, Constants.SECOND_BASE_URL))
                .build()
            response = chain.proceed(request)
        }
        response
    }
}

