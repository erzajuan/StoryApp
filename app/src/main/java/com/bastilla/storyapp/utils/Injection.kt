package com.bastilla.storyapp.utils

import android.content.Context

import com.bastilla.storyapp.api.ApiConfig
import com.bastilla.storyapp.database.StoryDatabase
import com.bastilla.storyapp.module.main.dataStore

object Injection {
    fun provideUserRepository(context: Context): Repository {
        val appExecutors = AppExecutors()
        val pref = SettingPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getDatabase(context)



        return Repository.getInstance(pref, apiService, appExecutors, storyDatabase)
    }
}