package com.example.streamanime.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UserTokenRequest(
    @SerializedName("user_token")
    val userToken: String
)