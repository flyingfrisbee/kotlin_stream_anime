package com.example.streamanime.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SearchTitleResponse(
    @SerializedName("anime_id")
    val animeId: String,
    val title: String
)