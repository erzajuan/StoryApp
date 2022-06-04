package com.bastilla.storyapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("loginResult")
    val loginResult: User? = null,

    @field:SerializedName("listStory")
    val listStory: ArrayList<StoryModel>? = null,

    ) : Parcelable

@Parcelize
data class User(
    @field:SerializedName("name")
    val name: String? = null,

    val email: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
) : Parcelable
