package com.example.streamanime.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class VideoUrlResponse(
    @SerializedName("video_url")
    val videoUrl: String
)