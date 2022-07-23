package com.example.streamanime.domain.model

import com.google.gson.annotations.SerializedName

data class RecentAnimeData(
    val imageUrl: String,
    val internalId: String,
    val latestEpisode: String,
    val title: String,
    val updatedTimestamp: Long
)
