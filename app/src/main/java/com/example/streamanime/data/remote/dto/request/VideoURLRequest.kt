package com.example.streamanime.data.remote.dto.request


import com.google.gson.annotations.SerializedName

data class VideoURLRequest(
    @SerializedName("episode_endpoint")
    val episodeEndpoint: String
)