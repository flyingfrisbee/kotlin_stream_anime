package com.example.streamanime.domain.model


import com.google.gson.annotations.SerializedName

data class UserTokenData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)