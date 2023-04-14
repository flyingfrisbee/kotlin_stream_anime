package com.example.streamanime.presentation.main_activity.fragments.bookmarked_anime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamanime.core.utils.setToGone
import com.example.streamanime.core.utils.setToVisible
import com.example.streamanime.databinding.ItemBookmarkAnimeBinding
import com.example.streamanime.domain.model.BookmarkedAnimeData

class BookmarkAnimeAdapter(
    private val onBookmarkClickListener: OnBookmarkClickListener,
) : RecyclerView.Adapter<BookmarkAnimeAdapter.ViewHolder>() {
    private val listContainer = mutableListOf<BookmarkedAnimeData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBookmarkAnimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listContainer[position])
    }

    override fun getItemCount(): Int {
        return listContainer.size
    }

    private fun notifyAdapterSetting(sizeBefore: Int, sizeAfter: Int, pos: Int) {
        if (sizeBefore == 0 || sizeBefore == sizeAfter) {
            notifyDataSetChanged()
            return
        }

        if (sizeBefore < sizeAfter) {
            notifyItemInserted(pos)
            notifyItemRangeChanged(pos, sizeAfter - pos)
            return
        }

        if (sizeAfter < sizeBefore) {
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, sizeAfter - pos)
            return
        }
    }

    fun populateData(input: List<BookmarkedAnimeData>, pos: Int) {
        val sizeBefore = listContainer.size
        listContainer.clear()
        listContainer.addAll(input)
        val sizeAfter = listContainer.size

        notifyAdapterSetting(sizeBefore, sizeAfter, pos)
    }

    fun getRecentAnimeFromPosition(pos: Int): BookmarkedAnimeData {
        return listContainer[pos]
    }

    interface OnBookmarkClickListener {
        fun onBookmarkClicked(data: BookmarkedAnimeData)
    }

    inner class ViewHolder(private val binding: ItemBookmarkAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BookmarkedAnimeData) {
            binding.apply {
                if (data.haveNewUpdate) {
                    tvNewEpisodeAvailable.setToVisible()
                } else {
                    tvNewEpisodeAvailable.setToGone()
                }
                Glide.with(itemView).load(data.imageUrl).into(ivBookmarkPoster)
                tvBookmarkTitle.text = data.title
                tvBookmarkLatestEpisode.text = "Episode ${data.latestEpisodeLocal} / ${data.latestEpisodeRemote}"
                clRoot.setOnClickListener {
                    onBookmarkClickListener.onBookmarkClicked(data)
                }
            }
        }
    }
}