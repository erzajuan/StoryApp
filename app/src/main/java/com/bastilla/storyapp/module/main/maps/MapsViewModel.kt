package com.bastilla.storyapp.module.main.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bastilla.storyapp.model.StoryModel
import com.bastilla.storyapp.utils.Repository
import kotlinx.coroutines.launch

class MapsViewModel(
    private val repository: Repository
) : ViewModel() {

    private var _stories = MutableLiveData<List<StoryModel>>()
    val stories: LiveData<List<StoryModel>> = _stories


    fun getToken() = repository.getToken()

    fun getLocation(token: String) {
        viewModelScope.launch {
            _stories.postValue(repository.getLocation(token))
        }
    }

}