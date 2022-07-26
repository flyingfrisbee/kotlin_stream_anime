package com.example.streamanime.presentation.stream_activity

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.fragment.TransparentDialogFragment
import com.example.streamanime.core.utils.setToGone
import com.example.streamanime.core.utils.setToVisible
import com.example.streamanime.databinding.ActivityStreamBinding
import com.example.streamanime.domain.model.AnimeDetailData
import com.example.streamanime.domain.model.enumerate.LoadingType
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StreamActivity : AppCompatActivity(), EpisodesAdapter.OnEpisodeClickListener {

    private lateinit var binding: ActivityStreamBinding
    private lateinit var mAdapter: EpisodesAdapter
    private val viewModel: StreamViewModel by viewModels()

    private val loadFragment = TransparentDialogFragment()
    private var mCustomView: View? = null
    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTextVisible(true)

        binding.apply {
            viewModel.apply {
                initializeRecyclerView()
                initializeWebview()
                retrieveIntentData()

                fabBookmark.setOnClickListener {
                    bookmarkAnime()
                }

                animeDetail.observe(this@StreamActivity) {
                    val previouslyWatchedEpisode = sharedPref.getInt(it.internalId, -2)
                    if (previouslyWatchedEpisode != -2) {
                        val episode = it.episodeList[previouslyWatchedEpisode]
                        episode.clicked = true
                        updateDataAndLoadVideo(episode.episodeForEndpoint, previouslyWatchedEpisode)
                    }

                    populateDataToViews(it)
                    nestedScrollView.setToVisible()
                    mAdapter.populateData(it.episodeList)
                }

                videoUrl.observe(this@StreamActivity) {
                    if (it.isNotBlank()) {
                        webView.loadUrl(it)
                        startTurnOffAdsCheckingJob()
                    }
                }

                loadingType.observe(this@StreamActivity) { type ->
                    when (type) {
                        is LoadingType.LoadAnimeDetail -> {
                            if (type.isLoading) {
                                pbLoad.setToVisible()
                                return@observe
                            }

                            pbLoad.setToGone()
                        }
                        is LoadingType.BookmarkAnime -> {
                            if (type.isLoading) {
                                loadFragment.show(supportFragmentManager, TransparentDialogFragment.TAG)
                                return@observe
                            }

                            loadFragment.dismiss()
                        }
                    }
                }

                successBookmark.observe(this@StreamActivity) {
                    if (it.isNotBlank()) {
                        Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }

                errorMessage.observe(this@StreamActivity) {
                    if (it.isNotBlank()) {
                        Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onClicked(endpoint: String, position: Int) {
        viewModel.apply {
            if (currentSelectedEpisodeIndex != position) {
                updateDataAndLoadVideo(endpoint, position)
            }
        }
    }

    private fun updateDataAndLoadVideo(endpoint: String, position: Int) {
        viewModel.apply {
            binding.tvInfo.text = "Currently watching episode ${animeDetail.value!!.episodeList[position].episodeForUi}"
            sharedPref.edit().putInt(animeDetail.value!!.internalId, position).apply()

            currentSelectedEpisodeIndex = position
            getVideoUrl(endpoint)
        }
    }

    private fun initializeRecyclerView() {
        mAdapter = EpisodesAdapter(this)
        binding.rvEpisodes.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@StreamActivity, 4)
            setHasFixedSize(true)
        }
    }

    private fun initializeWebview() {
        binding.webView.apply {
            viewModel.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = false

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest
                    ): WebResourceResponse? {
                        //added the host URL to -> list of non ads
                        if (!alreadyGotHostUrl && nonAds.size <= initialSize && request.url.toString().contains("streaming")) {
                            val url = request.url.toString().replace("https://", "")
                            nonAds.add(url.subSequence(0, url.indexOf("/")).toString())
                            alreadyGotHostUrl = true
                        }

                        if (adsChecking && isAdsURL(request.url.toString())) {
                            return WebResourceResponse("text/plain", "utf-8", null)
                        }

                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest
                    ): Boolean {
                        return true
                    }
                }
                webChromeClient = object : WebChromeClient() {

                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        return true
                    }

                    override fun onHideCustomView() {
                        (window.decorView as FrameLayout).removeView(mCustomView)
                        mCustomView = null
                        showSystemUI()
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        mCustomViewCallback!!.onCustomViewHidden()
                        mCustomViewCallback = null
                    }

                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        if (mCustomView != null) {
                            onHideCustomView()
                            return
                        }
                        mCustomView = view
                        requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        mCustomViewCallback = callback
                        (window.decorView as FrameLayout).addView(
                            mCustomView,
                            FrameLayout.LayoutParams(-1, -1)
                        )
                        hideSystemUI()
                    }
                }
            }
        }
    }

    private fun retrieveIntentData() {
        viewModel.apply {
            intent.apply {
                id = getStringExtra(Constants.ID)
                isInternalId = getBooleanExtra(Constants.IS_INTERNAL_ID, false)
            }

            getAnimeDetail()
        }
    }

    private fun populateDataToViews(data: AnimeDetailData) {
        data.apply {
            binding.apply {
                Glide.with(this@StreamActivity).load(imageUrl).into(ivAnimeDetailImage)
                tvAnimeDetailTitle.text = title
                tvAnimeDetailType.text = type
                tvAnimeDetailSummary.text = "Summary: $summary"
                tvAnimeDetailGenre.text = "Genre: $genre"
                tvAnimeDetailReleased.text = "Released: $releasedDate"
                tvAnimeDetailStatus.text = "Airing status: $airingStatus"
            }
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(
            WindowInsetsCompat.Type.systemBars())
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun makeStatusBarTextVisible(isLightUp: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLightUp
    }
}