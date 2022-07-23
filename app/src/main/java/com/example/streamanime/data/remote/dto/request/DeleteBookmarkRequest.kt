package com.example.streamanime.data.remote.dto.request

data class DeleteBookmarkRequest(
    val internal_id: String,
    val user_token: String
)