package com.kikulabs.storyapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kikulabs.storyapp.R
import com.kikulabs.storyapp.data.model.StoryModel
import com.kikulabs.storyapp.databinding.ItemRowStoriesBinding
import com.kikulabs.storyapp.ui.detail.DetailStoryActivity
import com.kikulabs.storyapp.ui.detail.DetailStoryActivity.Companion.EXTRA_STORY

class ListStoriesAdapter : RecyclerView.Adapter<ListStoriesAdapter.ListViewHolder>() {
    private val listStories = ArrayList<StoryModel>()

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowStoriesBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_row_stories, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val dataStory = listStories[position]

        with(holder) {

            Glide.with(itemView.context)
                .load(dataStory.photoUrl)
                .into(binding.ivStory)

            binding.tvUsername.text = dataStory.name
            binding.tvDesc.text = dataStory.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_STORY, dataStory)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivStory, "image"),
                        Pair(binding.tvUsername, "username"),
                        Pair(binding.tvDesc, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    override fun getItemCount(): Int = listStories.size

    @SuppressLint("NotifyDataSetChanged")
    fun setStory(story: ArrayList<StoryModel>) {
        listStories.clear()
        listStories.addAll(story)
        notifyDataSetChanged()
    }

}