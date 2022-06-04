package com.bastilla.storyapp.module.sign

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bastilla.storyapp.module.main.addstory.AddStoryViewModel
import com.bastilla.storyapp.module.main.home.HomeViewModel
import com.bastilla.storyapp.module.main.maps.MapsViewModel
import com.bastilla.storyapp.module.main.settings.SettingsViewModel
import com.bastilla.storyapp.utils.Injection
import com.bastilla.storyapp.utils.Repository

class ViewModelFactory private constructor(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignViewModel::class.java) -> SignViewModel(repository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(
                repository
            ) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(
                repository
            ) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideUserRepository(context)
                )
            }.also { instance = it }
    }
}