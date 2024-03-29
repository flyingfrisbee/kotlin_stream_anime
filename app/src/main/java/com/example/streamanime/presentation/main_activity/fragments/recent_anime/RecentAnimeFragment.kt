package com.example.streamanime.presentation.main_activity.fragments.recent_anime

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamanime.core.base.BaseFragment
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.setToGone
import com.example.streamanime.core.utils.setToVisible
import com.example.streamanime.databinding.FragmentRecentAnimeBinding
import com.example.streamanime.presentation.main_activity.MainViewModel
import com.example.streamanime.presentation.stream_activity.StreamActivity

class RecentAnimeFragment
    : BaseFragment<FragmentRecentAnimeBinding>(),
    SearchAnimeAdapter.OnSearchResultListener,
    RecentAnimeAdapter.OnRecentAnimeListener {

    private lateinit var recentAnimeAdapter: RecentAnimeAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var searchAdapter: SearchAnimeAdapter
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
                initializeSearchRecyclerView()
                initializeRecentAnimeRecyclerView()
                setListenersToACTV()

                ivCancel.setOnClickListener {
                    clearACTV()
                }

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

                titleResults.observe(viewLifecycleOwner) {
                    if (it.isNotEmpty()) {
                        searchAdapter.populateData(it)
                        rvSearchResult.setToVisible()
                        return@observe
                    }

                    rvSearchResult.setToGone()
                }

                recentAnimes.observe(viewLifecycleOwner) {
                    recentAnimeAdapter.populateData(it)
                }
            }
        }
    }

    override fun onTitleClicked(title: String, endpoint: String) {
        clearACTV()
        val intent = Intent(requireContext(), StreamActivity::class.java)
        intent.putExtra(Constants.DETAIL_ALT_TITLE, title)
        intent.putExtra(Constants.DETAIL_ALT_ENDPOINT, endpoint)
        startActivity(intent)
    }

    override fun onAnimeClicked(id: Int, latestEpisode: String) {
        clearACTV()
        viewModel.apply {
            updateField(id, latestEpisode) {
                getBookmarkedAnime()
            }
        }
        val isBookmarked = (viewModel.bookmarkedAnimes.value!!.firstOrNull { it.id == id } != null)

        val intent = Intent(requireContext(), StreamActivity::class.java)
        intent.putExtra(Constants.ANIME_ID_FOR_STREAM_ACTIVITY, id)
        intent.putExtra(Constants.ANIME_IS_BOOKMARKED, isBookmarked)
        startActivity(intent)
    }

    private fun initializeSearchRecyclerView() {
        searchAdapter = SearchAnimeAdapter(this)
        binding!!.apply {
            rvSearchResult.adapter = searchAdapter
            rvSearchResult.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initializeRecentAnimeRecyclerView() {
        recentAnimeAdapter = RecentAnimeAdapter(this)
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

    private fun setListenersToACTV() {
        binding!!.apply {
            viewModel.apply {
                actvSearchAnime.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {
                        if (p0!!.isBlank()) {
                            changeTitleResultsValue(emptyList())
                        } else {
                            startSearchJob(p0.toString())
                        }
                    }
                })

                actvSearchAnime.setOnFocusChangeListener { _, isFocused ->
                    if (isFocused) {
                        ivCancel.setToVisible()
                        return@setOnFocusChangeListener
                    }

                    ivCancel.setToGone()
                }
            }
        }
    }

    private fun clearACTV() {
        binding!!.apply {
            actvSearchAnime.setText("")
            hideSoftKeyboard()
            clRoot.requestFocus()
        }
    }

    private fun hideSoftKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}