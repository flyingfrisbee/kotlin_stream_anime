package com.example.streamanime.presentation.stream_activity

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.setToGone
import com.example.streamanime.core.utils.setToVisible
import com.example.streamanime.databinding.ActivityStreamBinding
import com.example.streamanime.domain.model.AnimeDetailData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber

@AndroidEntryPoint
class StreamActivity : AppCompatActivity(), EpisodesAdapter.OnEpisodeClickListener {

    private lateinit var binding: ActivityStreamBinding
    private lateinit var mAdapter: EpisodesAdapter
    private val viewModel: StreamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            viewModel.apply {
                initializeRecyclerView()
                retrieveIntentData()

                animeDetail.observe(this@StreamActivity) {
                    populateDataToViews(it)
                    nestedScrollView.setToVisible()
                    mAdapter.populateData(it.episodeList)
                }

                videoUrl.observe(this@StreamActivity) {
                    if (it.isNotBlank()) {
                        // load webview
                        Timber.i(it)
                    }
                }

                isLoading.observe(this@StreamActivity) {
                    if (it) {
                        pbLoad.setToVisible()
                        return@observe
                    }

                    pbLoad.setToGone()
                }

                errorMessage.observe(this@StreamActivity) {
                    if (it.isNotBlank()) {
                        Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onClicked(endpoint: String) {
        viewModel.getVideoUrl(endpoint)
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

    private fun initializeRecyclerView() {
        mAdapter = EpisodesAdapter(this)
        binding.rvEpisodes.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@StreamActivity, 4)
            setHasFixedSize(true)
        }
    }

    private fun populateDataToViews(data: AnimeDetailData) {
        data.apply {
            binding.apply {
                Glide.with(this@StreamActivity).load(imageUrl).into(ivAnimeDetailImage)
                tvAnimeDetailTitle.text = title
                tvAnimeDetailType.text = type
                tvAnimeDetailSummary.text = summary
                tvAnimeDetailGenre.text = genre
                tvAnimeDetailReleased.text = releasedDate
                tvAnimeDetailStatus.text = airingStatus
            }
        }
    }
}

//private lateinit var binding: ActivityStreamAnimeBinding
//private lateinit var animeID: String
//private lateinit var loadFirst: String
//private lateinit var adapter: StreamAnimeAdapter
//private lateinit var currentEpisode: String
//
//private val viewModel: StreamAnimeViewModel by viewModels()
//private var mCustomView: View? = null
//private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
//private var job: Job? = null
//
//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    binding = ActivityStreamAnimeBinding.inflate(layoutInflater)
//    setContentView(binding.root)
//
//    binding.apply {
//        viewModel.apply {
//            getStringExtras()
//
//            loadAnimeDetail()
//
//            initializeRecyclerView()
//
////                setupWebView()
//
//            setClickListenerToFAB()
//
//            streamAnimeState.observe(this@StreamAnimeActivity, {
//                when (it.isLoading) {
//                    true -> {
//                        pbLoad.showView()
//                        fabBookmark.hideView()
//                    }
//                    false -> {
//                        pbLoad.hideView()
//                        fabBookmark.showView()
//                    }
//                }
//
//                if (it.animeDetailResponse != null) {
//                    it.animeDetailResponse.apply {
//                        Glide.with(this@StreamAnimeActivity).load(imageURL).into(ivAnimeDetailImage)
//                        tvAnimeDetailTitle.text = title
//                        tvAnimeDetailSummary.text = "Summary:\n\n$summary"
//                        tvAnimeDetailGenre.text = "Genre: $genre"
//                        tvAnimeDetailReleased.text = "Released: $releasedYear"
//                        tvAnimeDetailStatus.text = "Status: $status"
//
//                        if (!status.contains("coming", true) && episodeListForURL.size != 0 && !videoURL.isNullOrBlank()) {
//                            showCurrentEpisodeTextInfo(episodeListForUI)
//
////                                loadVideoToWebView(videoURL, it.newVideoURL)
//                            if (it.newVideoURL == null) {
//                                loadVideoToWebView(videoURL)
//                            }
//
//                            adapter.populateData(episodeListForUI.map { EpisodeStateContainer(it) }, loadFirst)
//                        }
//                    }
//                }
//
//                if (it.newVideoURL != null) {
//                    loadVideoToWebView(it.newVideoURL)
////                        webView.loadUrl(it.newVideoURL)
//                }
//            })
//
//            bookmarkState.observe(this@StreamAnimeActivity, {
//                when (it.bookmarkResult) {
//                    is BookmarkResult.AlreadyBookmarked -> {
//                        Snackbar.make(binding.root, it.bookmarkResult.msg, Snackbar.LENGTH_SHORT).show()
//                    }
//                    is BookmarkResult.NotYetBookmarked -> {
//                        Snackbar.make(binding.root, it.bookmarkResult.msg, Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//            })
//
//            bookmarkedAnimes.observe(this@StreamAnimeActivity, {})
//        }
//    }
//}
//
//override fun onEpisodeClicked(pos: Int, index: Int) {
//    binding.apply {
//        viewModel.apply {
//            val animeDetail = streamAnimeState.value!!.animeDetailResponse!!
//
//            if (currentEpisode == animeDetail.episodeListForUI[pos]) {
//                Snackbar.make(root, "You are currently watching episode $currentEpisode!", Snackbar.LENGTH_SHORT).show()
//                return
//            }
//            currentEpisode = animeDetail.episodeListForUI[pos]
//            tvInfo.text = "Currently watching episode ${animeDetail.episodeListForUI[pos]}"
//            onEvent(StreamAnimeEvent.LoadVideoURL(animeDetail.episodeListForURL[pos]))
//            adapter.notifyItemChanged(index)
//            adapter.notifyItemChanged(pos)
//        }
//    }
//}
//
//private fun setClickListenerToFAB() {
//    binding.apply {
//        viewModel.apply {
//            fabBookmark.setOnClickListener {
//                val animeDetail = streamAnimeState.value?.animeDetailResponse
//                if (animeDetail != null) {
//                    val anime = RecentAnime(
//                        animeID = animeID,
//                        imageURL = animeDetail.imageURL,
//                        latestEpisode = animeDetail.maximumEpisode.toString(),
//                        title = animeDetail.title,
//                        timestamp = System.currentTimeMillis(),
//                        status = animeDetail.status
//                    )
//                    onEvent(StreamAnimeEvent.SaveAnimeToLocalDatabase(anime))
//                }
//            }
//        }
//    }
//}
//
//private fun loadVideoToWebView(videoURL: String) {
//    binding.webView.apply {
//        viewModel.apply {
//            adsChecking = true
//
//            settings.javaScriptEnabled = true
//            settings.domStorageEnabled = true
//
//            //enable hardware acceleration, hopefully faster load time webview
//            setLayerType(View.LAYER_TYPE_HARDWARE, null)
//
//            webViewClient = object : WebViewClient() {
//
//                override fun shouldInterceptRequest(
//                    view: WebView?,
//                    request: WebResourceRequest
//                ): WebResourceResponse? {
//                    //added the host URL to -> list of not ads
//                    if (!alreadyGotURL && listOfNotAds.size <= 6 && request.url.toString().contains("streaming")) {
//                        val url = request.url.toString().replace("https://", "")
//                        listOfNotAds.add(url.subSequence(0, url.indexOf("/")).toString())
//                        alreadyGotURL = true
//                    }
//
//                    if (adsChecking && isAdsURL(request.url.toString())) {
//                        return WebResourceResponse("text/plain", "utf-8", null)
//                    }
//
//                    return super.shouldInterceptRequest(view, request)
//                }
//
//                override fun shouldOverrideUrlLoading(
//                    view: WebView?,
//                    request: WebResourceRequest
//                ): Boolean {
//                    return true
//                }
//            }
//            webChromeClient = object : WebChromeClient() {
//
//                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//                    return true
//                }
//
//                override fun onHideCustomView() {
////                    super.onHideCustomView()
//                    (window.decorView as FrameLayout).removeView(mCustomView)
//                    mCustomView = null
//                    showSystemUI()
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//                    mCustomViewCallback!!.onCustomViewHidden()
//                    mCustomViewCallback = null
//                }
//
//                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
////                    super.onShowCustomView(view, callback)
//                    if (mCustomView != null) {
//                        onHideCustomView()
//                        return
//                    }
//                    mCustomView = view
//                    requestedOrientation =
//                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//                    mCustomViewCallback = callback
//                    (window.decorView as FrameLayout).addView(
//                        mCustomView,
//                        FrameLayout.LayoutParams(-1, -1)
//                    )
//                    hideSystemUI()
//                }
//            }
//
//            loadUrl(videoURL)
//
//            job?.cancel()
//            job = lifecycleScope.launch {
//                withContext(Dispatchers.IO) {
//                    delay(120000)
//                    adsChecking = false
//                }
//            }
//        }
//    }
//}
//
//private fun hideSystemUI() {
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
////        }
//    WindowCompat.setDecorFitsSystemWindows(window, false)
//    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
//        controller.hide(WindowInsetsCompat.Type.systemBars())
//        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    }
//}
//
//private fun showSystemUI() {
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
////        }
//    WindowCompat.setDecorFitsSystemWindows(window, true)
//    WindowInsetsControllerCompat(window, window.decorView).show(
//        WindowInsetsCompat.Type.systemBars())
//}
//
////    private fun loadVideoToWebView(videoURL: String, newVideoURL: String?) {
////        if (newVideoURL == null) {
////            binding.webView.loadUrl(videoURL)
////        }
////    }
//
//private fun showCurrentEpisodeTextInfo(episodeListForUI: List<String>) {
//    binding.apply {
//        if (tvInfo.text.isNullOrBlank() && episodeListForUI.size != 0) {
//            var index = 0
//            if (loadFirst == "false") {
//                index = (episodeListForUI.size - 1)
//            }
//            tvInfo.text = "Currently watching episode ${episodeListForUI[index]}"
//            currentEpisode = episodeListForUI[index]
//        } else if (episodeListForUI.size == 0) {
//            tvInfo.text = "Anime not available yet"
//        }
//    }
//}
//
//private fun getStringExtras() {
//    animeID = intent.getStringExtra("anime_id")!!
//    loadFirst = intent.getStringExtra("load_first_episode")!!
//}
//
//private fun loadAnimeDetail() {
//    viewModel.onEvent(StreamAnimeEvent.LoadAnimeDetail(
//        animeID, PostAnimeDetailRequest(
//            loadFirst
//        ))
//    )
//}