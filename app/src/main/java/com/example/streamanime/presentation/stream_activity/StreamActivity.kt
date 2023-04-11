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
                    val canBookmark = (animeID != -1)
                    if (canBookmark) {
                        bookmarkAnime()
                    }
                }

                animeDetail.observe(this@StreamActivity) {
                    val previouslyWatchedEpisode = sharedPref.getInt(it.id.toString(), -2)
                    if (previouslyWatchedEpisode != -2) {
                        val episode = it.episodes[previouslyWatchedEpisode]
                        episode.clicked = true
                        updateDataAndLoadVideo(episode.endpoint, previouslyWatchedEpisode)
                    }

                    populateDataToViews(it)
                    nestedScrollView.setToVisible()
                    mAdapter.populateData(it.episodes)
                }

                videoUrl.observe(this@StreamActivity) {
                    if (it.isNotBlank()) {
                        webView.stopLoading()
                        alreadyGotCdnURL = false
                        if (nonAds.size == initialSize + 2) {
                            nonAds.removeLast()
                        }
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
            binding.tvInfo.text = "Currently watching episode ${animeDetail.value!!.episodes[position].text}"
            sharedPref.edit().putInt(animeDetail.value!!.id.toString(), position).apply()

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
                        val url = request.url.toString()
                        //added the host URL to -> list of non ads
                        if (!alreadyGotHostUrl && nonAds.size <= initialSize && url.contains("streaming")) {
                            val processedURL = url.replace("https://", "")
                            nonAds.add(processedURL.subSequence(0, processedURL.indexOf("/")).toString())
                            alreadyGotHostUrl = true
                        }

                        // save the cdn address
                        if (!alreadyGotCdnURL && url.contains(".m3u8")) {
                            val processedURL = url.replace("https://", "")
                            nonAds.add(processedURL.subSequence(0, processedURL.indexOf("/")).toString())
                            alreadyGotCdnURL = true
                        }

                        if (adsChecking && isAdsURL(url)) {
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
                detailAltTitle = getStringExtra(Constants.DETAIL_ALT_TITLE)
                detailAltEndpoint = getStringExtra(Constants.DETAIL_ALT_ENDPOINT)
                animeID = getIntExtra(Constants.ANIME_ID_FOR_STREAM_ACTIVITY, -1)
            }

            val doHideBookmarkButton = (animeID == -1)
            if (doHideBookmarkButton) {
                binding.fabBookmark.setToGone()
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
                tvAnimeDetailReleased.text = "Released: $airingYear"
                tvAnimeDetailStatus.text = "Airing status: $status"
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