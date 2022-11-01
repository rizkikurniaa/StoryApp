package com.kikulabs.storyapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    val id: String?,
    val name: String?,
    val description: String?,
    val photoUrl: String?,
    val createdAt: String?,
) : Parcelable