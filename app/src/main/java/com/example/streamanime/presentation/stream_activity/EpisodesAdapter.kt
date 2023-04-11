package com.example.streamanime.presentation.stream_activity

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.streamanime.databinding.ItemEpisodeBinding
import com.example.streamanime.domain.model.EpisodeData

class EpisodesAdapter(
    private val onEpisodeClickListener: OnEpisodeClickListener
) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {
    private val listContainer = mutableListOf<EpisodeData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEpisodeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listContainer[position], position)
    }

    override fun getItemCount(): Int {
        return listContainer.size
    }

    fun populateData(input: List<EpisodeData>) {
        listContainer.clear()
        listContainer.addAll(input)
        notifyDataSetChanged()
    }

    interface OnEpisodeClickListener {
        fun onClicked(endpoint: String, position: Int)
    }

    inner class ViewHolder(private val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EpisodeData, position: Int) {
            binding.apply {
                if (data.clicked) {
                    btnEpisode.setBackgroundColor(Color.parseColor("#FFC4C4"))
                } else {
                    btnEpisode.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
                btnEpisode.text = "Episode\n${data.text}"
                btnEpisode.setOnClickListener {
                    val index = listContainer.indexOfFirst { it.clicked }
                    if (index != -1) {
                        listContainer[index].clicked = false
                        notifyItemChanged(index)
                    }
                    data.clicked = true
                    notifyItemChanged(position)
                    onEpisodeClickListener.onClicked(data.endpoint, position)
                }
            }
        }
    }
}