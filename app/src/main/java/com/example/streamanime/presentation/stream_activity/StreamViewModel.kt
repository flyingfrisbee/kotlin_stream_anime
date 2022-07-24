package com.example.streamanime.presentation.stream_activity

import androidx.lifecycle.ViewModel
import com.example.streamanime.domain.repository.AnimeServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val repo: AnimeServicesRepository
) : ViewModel() {
    var id: String? = null
    var isInternalId = false
}