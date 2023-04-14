package com.example.streamanime.presentation.main_activity.fragments.bookmarked_anime

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamanime.R
import com.example.streamanime.core.base.BaseFragment
import com.example.streamanime.core.utils.Constants
import com.example.streamanime.core.utils.fragment.TransparentDialogFragment
import com.example.streamanime.databinding.FragmentBookmarkedAnimeBinding
import com.example.streamanime.domain.model.BookmarkedAnimeData
import com.example.streamanime.presentation.main_activity.MainViewModel
import com.example.streamanime.presentation.stream_activity.StreamActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class BookmarkedAnimeFragment : BaseFragment<FragmentBookmarkedAnimeBinding>(), BookmarkAnimeAdapter.OnBookmarkClickListener {

    private lateinit var adapter: BookmarkAnimeAdapter
    private val loadingFragment = TransparentDialogFragment()
    private val viewModel: MainViewModel by activityViewModels()
    private val icon: Drawable by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
    }

    override fun initViewBinding(): FragmentBookmarkedAnimeBinding {
        return FragmentBookmarkedAnimeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated() {
        binding!!.apply {
            viewModel.apply {
                initializeRecyclerView()

                val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        updatePosition(position)

                        val bookmarkedAnime = adapter.getRecentAnimeFromPosition(position)

                        deleteBookmarkedAnime(bookmarkedAnime) {
                            Snackbar.make(root, "Successfully deleted ${bookmarkedAnime.title}", Snackbar.LENGTH_LONG).apply {
                                setAction("Undo") {
                                    insertToBookmark(bookmarkedAnime)
                                }
                                setAnchorView(requireActivity().findViewById<BottomNavigationView>(R.id.btmNavView))
                                show()
                            }
                        }
                    }

                    override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                    ) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            super.onChildDraw(
                                c,
                                recyclerView,
                                viewHolder,
                                dX,
                                dY,
                                actionState,
                                isCurrentlyActive
                            )

                            val itemView = viewHolder.itemView
                            val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                            val iconBottom = iconTop + icon.intrinsicHeight
                            val iconLeft = itemView.right - icon.intrinsicWidth
                            val iconRight = itemView.right

                            if (dX < 0) {
                                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                                getBackground().setBounds((itemView.right.toFloat() + dX).toInt(), itemView.top, itemView.right, itemView.bottom)
                            } else if (dX > 0) {
                                icon.setBounds(itemView.left, iconTop, itemView.left + icon.intrinsicWidth, iconBottom)
                                getBackground().setBounds(itemView.left, itemView.top, (itemView.left.toFloat() + dX).toInt(), itemView.bottom)
                            }
                            getBackground().draw(c)
                            icon.draw(c)
                        }
                    }
                }

                ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvBookmarkAnimes)

                bookmarkedAnimes.observe(viewLifecycleOwner) {
                    adapter.populateData(it, getPosition())
                }

                bookmarkLoading.observe(viewLifecycleOwner) {
                    if (it) {
                        loadingFragment.show(childFragmentManager, TransparentDialogFragment.TAG)
                        return@observe
                    }

                    loadingFragment.dialog?.let {
                        if (it.isShowing) {
                            loadingFragment.dismiss()
                        }
                    }
                    getBookmarkedAnime()
                }
            }
        }
    }

    override fun onBookmarkClicked(data: BookmarkedAnimeData) {
        viewModel.updateField(data.id) {
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra(Constants.ANIME_ID_FOR_STREAM_ACTIVITY, data.id)
            intent.putExtra(Constants.ANIME_IS_BOOKMARKED, true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBookmarkedAnime()
    }

    private fun initializeRecyclerView() {
        adapter = BookmarkAnimeAdapter(this)
        binding!!.apply {
            rvBookmarkAnimes.adapter = adapter
            rvBookmarkAnimes.layoutManager = LinearLayoutManager(requireContext())
            rvBookmarkAnimes.setHasFixedSize(true)
        }
    }
}