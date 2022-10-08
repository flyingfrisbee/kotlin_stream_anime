package com.example.streamanime.di

import com.example.streamanime.core.utils.Constants
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

fun handleRedirectInterceptor(): Interceptor {
    return Interceptor { chain ->
        var request: Request = chain.request()
        var response: Response = chain.proceed(request)
        if (response.code != 200 && !request.url.toString().contains(Constants.SECOND_BASE_URL)) {
            response.close()
            request = request.newBuilder()
                .url(request.url.toString().replace(Constants.BASE_URL, Constants.SECOND_BASE_URL))
                .build()
            response = chain.proceed(request)
        }
        response
    }
}
