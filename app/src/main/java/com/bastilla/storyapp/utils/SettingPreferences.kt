package com.bastilla.storyapp.utils

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val FIRST_TIME_KEY = booleanPreferencesKey("first_time")
    private val THEME_MODE_KEY = booleanPreferencesKey("theme_mode")
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")

    fun isFirstTime(): Flow<Boolean> = dataStore.data.map {
        it[FIRST_TIME_KEY] ?: true
    }

    suspend fun setIsFirstTime(firstTime: Boolean) {
        dataStore.edit {
            it[FIRST_TIME_KEY] = firstTime
        }
    }

    fun getThemeMode(): Flow<Boolean> = dataStore.data.map {
        it[THEME_MODE_KEY] ?: false
    }

    suspend fun setThemeMode(themeMode: Boolean) {
        dataStore.edit {
            it[THEME_MODE_KEY] = themeMode
        }
    }

    fun getUserToken(): Flow<String> = dataStore.data.map {
        it[USER_TOKEN_KEY] ?: DEFAULT_VALUE
    }

    suspend fun setUserToken(token: String) {
        dataStore.edit {
            it[USER_TOKEN_KEY] = token
            Log.e("SettingPreference", "Token saved! saveUserToken: $token")
        }
    }

    fun getUserName(): Flow<String> = dataStore.data.map {
        it[USER_NAME_KEY] ?: DEFAULT_VALUE
    }

    suspend fun setUserName(name: String) {
        dataStore.edit {
            it[USER_NAME_KEY] = name
        }
    }

    fun getUserEmail(): Flow<String> = dataStore.data.map {
        it[USER_EMAIL_KEY] ?: DEFAULT_VALUE
    }

    suspend fun setUserEmail(email: String) {
        dataStore.edit {
            it[USER_EMAIL_KEY] = email
        }
    }

    suspend fun clearCache() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null
        const val DEFAULT_VALUE = "not_set_yet"

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}