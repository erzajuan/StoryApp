package com.bastilla.storyapp.module.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bastilla.storyapp.model.User
import com.bastilla.storyapp.model.UserResponse
import com.bastilla.storyapp.utils.Event
import com.bastilla.storyapp.utils.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignViewModel(private val repository: Repository) : ViewModel() {

    private var _user = MutableLiveData<Event<User>>()
    val user: LiveData<Event<User>> = _user

    private var _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private var _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private var _error = MutableLiveData<Event<Boolean>>()
    val error: LiveData<Event<Boolean>> = _error

    fun login(email: String, password: String) {
        _isLoading.value = Event(true)
        val client = repository.login(email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                _isLoading.value = Event(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()?.loginResult
                    _error.value = Event(false)
                    repository.appExecutors.networkIO.execute {
                        _user.postValue(Event(responseBody!!))
                    }
                } else {
                    _message.value = Event(response.message())
                    _error.value = Event(true)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _message.value = Event(t.message.toString())
                _error.value = Event(true)
            }
        })
    }

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        _isLoading.value = Event(true)
        val client = repository.register(name, email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                _isLoading.value = Event(false)
                if (response.isSuccessful) {
                    _error.value = Event(false)
                } else {
                    _message.value = Event(response.message())
                    _error.value = Event(true)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _message.value = Event(t.message.toString())
                _error.value = Event(true)
            }
        })
    }

    fun getUserToken() = repository.getToken()

    fun setUserToken(token: String?) {
        viewModelScope.launch {
            if (token != null) {
                repository.setToken(token)
            }
        }
    }

    fun setUserName(name: String?) {
        viewModelScope.launch {
            if (name != null) {
                repository.setUserName(name)
            }
        }
    }

    fun setUserEmail(email: String) {
        viewModelScope.launch {
            repository.setUserEmail(email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }

}