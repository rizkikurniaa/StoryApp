package com.kikulabs.storyapp.data.response

import com.kikulabs.storyapp.data.model.StoryModel

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: ArrayList<StoryModel>,
)
