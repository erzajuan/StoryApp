package com.bastilla.storyapp.api

import com.bastilla.storyapp.model.StoryResponse
import com.bastilla.storyapp.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("register")
    fun userRegister(
        @Body user: Map<String, String>
    ): Call<UserResponse>

    @POST("login")
    fun userLogin(
        @Body user: Map<String, String>
    ): Call<UserResponse>

    @GET("stories")
    fun getUserStories(): Call<UserResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
    fun postUserStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<UserResponse>

    @GET("stories?location=1")
    suspend fun getLocation(
        @Header("Authorization") token: String
    ): StoryResponse
}