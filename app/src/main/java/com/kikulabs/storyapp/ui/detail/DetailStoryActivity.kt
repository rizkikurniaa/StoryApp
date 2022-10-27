package com.kikulabs.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.kikulabs.storyapp.data.model.StoryModel
import com.kikulabs.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val selectedStory: StoryModel? = intent.getParcelableExtra(EXTRA_STORY)

        if (selectedStory != null) {
            Glide.with(this)
                .load(selectedStory.photoUrl)
                .into(binding.ivStory)

            binding.tvUsername.text = selectedStory.name
            binding.tvDescription.text = selectedStory.description
        }
    }
}