package com.example.streamanime.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RecentAnimeResponse(
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("internal_id")
    val internalId: String,
    @SerializedName("latest_episode")
    val latestEpisode: String,
    val title: String,
    @SerializedName("updated_timestamp")
    val updatedTimestamp: Long
)