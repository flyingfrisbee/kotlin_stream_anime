package com.example.streamanime.presentation.main_activity.fragments.recent_anime

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamanime.databinding.ItemRecentAnimeBinding
import com.example.streamanime.domain.model.RecentAnimeData
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class RecentAnimeAdapter(
    private val onRecentAnimeListener: OnRecentAnimeListener
) : RecyclerView.Adapter<RecentAnimeAdapter.ViewHolder>() {

    private val listContainer = mutableListOf<RecentAnimeData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecentAnimeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listContainer[position])
    }

    override fun getItemCount(): Int {
        return listContainer.size
    }

    fun populateData(input: List<RecentAnimeData>) {
        listContainer.clear()
        listContainer.addAll(input)
        notifyDataSetChanged()
    }

    interface OnRecentAnimeListener {
        fun onAnimeClicked(id: Int)
    }

    inner class ViewHolder(val binding: ItemRecentAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RecentAnimeData) {
            binding.apply {
                data.apply {
                    clParent.setOnClickListener {
                        onRecentAnimeListener.onAnimeClicked(id)
                    }

                    Glide.with(binding.root).load(imageUrl).into(ivRecentAnime)
                    tvTitle.text = title
                    tvEpisode.text = "Episode $latestEpisode"
                    tvUpdatedTimestamp.text = "Updated $updatedAtTimestamp"
                }
            }
        }
    }
}