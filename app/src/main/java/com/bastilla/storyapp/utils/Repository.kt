package com.bastilla.storyapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bastilla.storyapp.api.ApiInterceptor
import com.bastilla.storyapp.api.ApiService
import com.bastilla.storyapp.model.StoryModel
import com.bastilla.storyapp.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import androidx.paging.*
import com.bastilla.storyapp.database.StoryDatabase
import com.bastilla.storyapp.module.main.home.pagging.StoryRemoteMediator
import retrofit2.converter.gson.GsonConverterFactory

class Repository(
    private val pref: SettingPreferences,
    private val apiService: ApiService,
    val appExecutors: AppExecutors,
    private val storyDatabase: StoryDatabase
) {

    fun getTheme(): LiveData<Boolean> = pref.getThemeMode().asLiveData()
    suspend fun setTheme(value: Boolean) = pref.setThemeMode(value)

    fun getToken(): LiveData<String> = pref.getUserToken().asLiveData()
    suspend fun setToken(value: String) = pref.setUserToken(value)

    fun getUserName(): LiveData<String> = pref.getUserName().asLiveData()
    suspend fun setUserName(value: String) = pref.setUserName(value)

    fun getUserEmail(): LiveData<String> = pref.getUserEmail().asLiveData()
    suspend fun setUserEmail(value: String) = pref.setUserEmail(value)

    fun getIsFirstTime(): LiveData<Boolean> = pref.isFirstTime().asLiveData()
    suspend fun setIsFirstTime(value: Boolean) = pref.setIsFirstTime(value)

    suspend fun clearCache() = pref.clearCache()

    fun login(email: String, password: String): Call<UserResponse> {
        val user: Map<String, String> = mapOf(
            "email" to email,
            "password" to password
        )

        return apiService.userLogin(user)
    }

    fun register(name: String, email: String, password: String): Call<UserResponse> {
        val user: Map<String, String> = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )

        return apiService.userRegister(user)
    }

    fun getStories(token: String): Call<UserResponse> {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor(token))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val mApiService = retrofit.create(ApiService::class.java)
        return mApiService.getUserStories()
    }

    fun getPaggingStories(token: String
    ): LiveData<PagingData<StoryModel>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllLocalStory()
            }
        ).liveData
    }

    fun addStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        token: String,
        lat: Float,
        lon: Float,
    ): Call<UserResponse> {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor(token))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val mApiService = retrofit.create(ApiService::class.java)
        return mApiService.postUserStory(photo, description, lat, lon)
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getLocation(token: String): List<StoryModel>{
        return apiService.getLocation("bearer $token").listStory as List<StoryModel>
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        @JvmStatic
        fun getInstance(
            pref: SettingPreferences,
            apiService: ApiService,
            appExecutors: AppExecutors,
        storyDatabase: StoryDatabase
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(pref, apiService, appExecutors, storyDatabase)
            }.also { instance = it }

    }
}