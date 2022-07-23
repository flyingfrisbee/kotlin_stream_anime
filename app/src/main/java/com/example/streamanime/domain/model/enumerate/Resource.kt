package com.example.streamanime.domain.model.enumerate

sealed class Resource<T>(val data: T? = null, val msg: String? = null) {
    class Loading<T>() : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(msg: String) : Resource<T>(msg = msg)
}
