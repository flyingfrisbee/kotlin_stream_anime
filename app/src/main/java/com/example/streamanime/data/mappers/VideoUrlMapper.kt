package com.example.streamanime.data.mappers

import com.example.streamanime.data.remote.dto.response.VideoUrlResponse
import com.example.streamanime.domain.model.VideoUrlData

fun VideoUrlResponse.toData(): VideoUrlData {
    return VideoUrlData(
        videoUrl = videoUrl
    )
}