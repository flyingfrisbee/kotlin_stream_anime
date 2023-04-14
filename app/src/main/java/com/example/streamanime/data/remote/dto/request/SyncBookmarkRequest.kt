package com.example.streamanime.data.remote.dto.request


import com.google.gson.annotations.SerializedName

data class SyncBookmarkRequest(
    @SerializedName("anime_ids")
    val animeIds: List<Int>
)