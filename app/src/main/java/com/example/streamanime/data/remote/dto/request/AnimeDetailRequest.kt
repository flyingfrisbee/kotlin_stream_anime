package com.example.streamanime.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class AnimeDetailRequest(
    @SerializedName("is_internal_id")
    val isInternalId: Boolean
)