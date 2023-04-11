package com.example.streamanime.domain.model


import com.google.gson.annotations.SerializedName

data class RefreshTokenData(
    @SerializedName("auth_token")
    val authToken: String
)