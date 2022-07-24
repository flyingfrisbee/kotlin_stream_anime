package com.example.streamanime.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class BookmarkedAnimeResponse(
    @SerializedName("internal_id")
    val internalId: String,
    @SerializedName("latest_episode")
    val latestEpisode: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("image_url")
    val imageUrl: String
)