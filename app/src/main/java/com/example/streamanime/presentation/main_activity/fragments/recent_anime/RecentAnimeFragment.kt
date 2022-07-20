package com.example.streamanime.presentation.main_activity.fragments.recent_anime

import android.content.SharedPreferences
import androidx.fragment.app.activityViewModels
import com.example.streamanime.core.base.BaseFragment
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.databinding.FragmentRecentAnimeBinding
import com.example.streamanime.presentation.main_activity.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class RecentAnimeFragment : BaseFragment<FragmentRecentAnimeBinding>() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initViewBinding(): FragmentRecentAnimeBinding {
        return FragmentRecentAnimeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated() {
        binding.apply {
            viewModel.apply {



                errorMessage.observe(viewLifecycleOwner) {
                    if (it.isNotBlank()) {
                        Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}