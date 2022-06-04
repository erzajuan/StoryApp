package com.bastilla.storyapp.module.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bastilla.storyapp.model.StoryModel
import com.bastilla.storyapp.utils.Repository

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getStories(token: String): LiveData<PagingData<StoryModel>> =
        repository.getPaggingStories(token).cachedIn(viewModelScope)

    fun getUserToken() = repository.getToken()
}