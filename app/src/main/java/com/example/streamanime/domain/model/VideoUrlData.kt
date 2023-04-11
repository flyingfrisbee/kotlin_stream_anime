package com.example.streamanime.domain.model

import com.google.gson.annotations.SerializedName

data class VideoUrlData(
    @SerializedName("video_url")
    val videoUrl: String
)
