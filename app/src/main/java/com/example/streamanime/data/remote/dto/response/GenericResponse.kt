package com.example.streamanime.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class GenericResponse<T>(
    val data: T,
    val message: String,
    @SerializedName("status_code")
    val statusCode: Int
)