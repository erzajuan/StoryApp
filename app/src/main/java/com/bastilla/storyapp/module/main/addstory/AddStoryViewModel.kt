package com.bastilla.storyapp.module.main.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bastilla.storyapp.model.UserResponse
import com.bastilla.storyapp.utils.Event
import com.bastilla.storyapp.utils.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel (private val repository: Repository): ViewModel() {
    private var _error = MutableLiveData<Event<Boolean>>()
    val error: LiveData<Event<Boolean>> = _error

    private var _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private var _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading


    fun uploadStory(photo: MultipartBody.Part, description: RequestBody, token: String, lat: Float, lon: Float) {
        _isLoading.value = Event(true)
        val client = repository.addStory(photo, description, token, lat, lon)
        client.enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                repository.appExecutors.networkIO.execute {
                    if (response.isSuccessful) {
                        _error.postValue(Event(false))
                        _isLoading.postValue(Event(false))
                    } else {
                        _error.postValue(Event(true))
                        _message.postValue(Event(response.message()))
                        _isLoading.postValue(Event(false))
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _error.value = Event(true)
                _message.value = Event(t.message.toString())
            }
        })
    }
}