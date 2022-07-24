package com.example.streamanime.presentation.stream_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.databinding.ActivityStreamBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StreamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStreamBinding
    private val viewModel: StreamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            viewModel.apply {
                retrieveIntentData()


            }
        }
    }

    private fun retrieveIntentData() {
        viewModel.apply {
            intent.apply {
                id = getStringExtra(Constants.ID)
                isInternalId = getBooleanExtra(Constants.IS_INTERNAL_ID, false)
            }
        }
    }
}