package com.example.streamanime.data.remote.dto.request


import com.google.gson.annotations.SerializedName

data class AnimeDetailAltRequest(
    @SerializedName("endpoint")
    val endpoint: String,
    @SerializedName("title")
    val title: String
)