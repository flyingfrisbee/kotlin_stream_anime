package com.example.streamanime.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class DeleteBookmarkRequest(
    @SerializedName("internal_id")
    val internalId: String,
    @SerializedName("user_token")
    val userToken: String
)