package com.example.streamanime.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateBookmarkRequest(
    @SerializedName("internal_id")
    val internalId: String,
    @SerializedName("latest_episode")
    val latestEpisode: String,
    @SerializedName("user_token")
    val userToken: String
)