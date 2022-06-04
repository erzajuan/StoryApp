package com.bastilla.storyapp.module.main.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bastilla.storyapp.utils.Repository
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: Repository) : ViewModel() {
    fun getThemeMode(): LiveData<Boolean> = userRepository.getTheme()

    fun saveThemeMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            userRepository.setTheme(isDarkMode)
        }
    }

    fun getUserName(): LiveData<String> = userRepository.getUserName()

    fun getUserEmail(): LiveData<String> = userRepository.getUserEmail()

    fun getIsFirstTime(): LiveData<Boolean> = userRepository.getIsFirstTime()
    fun saveIsFirstTime(value: Boolean) {
        viewModelScope.launch {
            userRepository.setIsFirstTime(value)
        }
    }

}