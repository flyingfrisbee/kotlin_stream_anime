package com.example.streamanime.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class DeleteBookmarkRequest(
    @SerializedName("anime_id")
    val animeId: Int,
    @SerializedName("user_token")
    val userToken: String
)