package com.example.streamanime.presentation.main_activity.fragments.recent_anime

import android.content.SharedPreferences
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamanime.core.base.BaseFragment
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.setToGone
import com.example.streamanime.core.utils.setToVisible
import com.example.streamanime.databinding.FragmentRecentAnimeBinding
import com.example.streamanime.presentation.main_activity.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber
import javax.inject.Inject

class RecentAnimeFragment : BaseFragment<FragmentRecentAnimeBinding>() {

    private lateinit var recentAnimeAdapter: RecentAnimeAdapter
    private lateinit var layoutManager: GridLayoutManager
    private val viewModel: MainViewModel by activityViewModels()

    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun initViewBinding(): FragmentRecentAnimeBinding {
        return FragmentRecentAnimeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated() {
        binding!!.apply {
            viewModel.apply {
                initializeRecentAnimeRecyclerView()

                swipeRefreshLayout.setOnRefreshListener {
                    isLoading.value?.let {
                        if (it) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            reloadRecentAnimes()
                        }
                    }
                }

                isLoading.observe(viewLifecycleOwner) {
                    if (it) {
                        progressBar.setToVisible()
                        return@observe
                    }

                    progressBar.setToGone()
                    swipeRefreshLayout.isRefreshing = it
                }

                recentAnimes.observe(viewLifecycleOwner) {
                    recentAnimeAdapter.populateData(it)
                }
            }
        }
    }

    private fun initializeRecentAnimeRecyclerView() {
        recentAnimeAdapter = RecentAnimeAdapter()
        layoutManager = GridLayoutManager(requireContext(), 2)

        binding!!.apply {
            rvRecentAnime.adapter = recentAnimeAdapter
            rvRecentAnime.layoutManager = layoutManager
            rvRecentAnime.setHasFixedSize(true)

            rvRecentAnime.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount()
                        totalItemCount = layoutManager.getItemCount()
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

                        viewModel.isLoading.value?.let {
                            if (!it) {
                                val haveReachedLastItem = (visibleItemCount + pastVisiblesItems >= totalItemCount)
                                if (haveReachedLastItem) {
                                    viewModel.getRecentAnimes()
                                }
                            }
                        }
                    }
                }
            })
        }
    }
}