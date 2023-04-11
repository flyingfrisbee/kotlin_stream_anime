package com.example.streamanime.presentation.main_activity.fragments.recent_anime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.streamanime.databinding.ItemSearchAnimeBinding
import com.example.streamanime.domain.model.SearchTitleData

class SearchAnimeAdapter(
    private val onSearchResultListener: OnSearchResultListener
) : RecyclerView.Adapter<SearchAnimeAdapter.ViewHolder>() {

    private val listContainer = mutableListOf<SearchTitleData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSearchAnimeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchAnime = listContainer[position]
        holder.bind(searchAnime)
    }

    override fun getItemCount(): Int {
        return listContainer.size
    }

    fun populateData(input: List<SearchTitleData>) {
        listContainer.clear()
        listContainer.addAll(input)
        notifyDataSetChanged()
    }

    interface OnSearchResultListener {
        fun onTitleClicked(title: String, endpoint: String)
    }

    inner class ViewHolder(private val binding: ItemSearchAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SearchTitleData) {
            binding.apply {
                tvSearchAnimeTitle.text = data.title
                clRoot.setOnClickListener {
                    onSearchResultListener.onTitleClicked(data.title, data.endpoint)
                }
            }
        }
    }
}