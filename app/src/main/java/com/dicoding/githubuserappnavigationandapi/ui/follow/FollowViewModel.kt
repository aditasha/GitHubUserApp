package com.dicoding.githubuserappnavigationandapi.ui.follow

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuserappnavigationandapi.api.ApiConfig
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import kotlinx.coroutines.launch

class FollowViewModel(user_id: String) : ViewModel() {
    private val _userFollowers = MutableLiveData<ArrayList<UserItem>>()
    val userFollowers: LiveData<ArrayList<UserItem>> = _userFollowers

    private val _userFollowing = MutableLiveData<ArrayList<UserItem>>()
    val userFollowing: LiveData<ArrayList<UserItem>> = _userFollowing

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            userFollowers(user_id)
        }
        viewModelScope.launch {
            userFollowing(user_id)
        }
    }

    private suspend fun userFollowers(user_id: String) {
        _isLoading.value = true
        try {
            val client = ApiConfig.getApiService().userFollowers(user_id)
            _userFollowers.postValue(client)
            _isLoading.value = false
        } catch (e: Throwable) {
            Log.e(ContentValues.TAG, "onFailure: ${e.message.toString()}")
            _isLoading.value = false
        }
    }

    private suspend fun userFollowing(user_id: String) {
        _isLoading.value = true
        try {
            val client = ApiConfig.getApiService().userFollowing(user_id)
            _userFollowing.postValue(client)
            _isLoading.value = false
        } catch (e: Throwable) {
            Log.e(ContentValues.TAG, "onFailure: ${e.message.toString()}")
            _isLoading.value = false
        }
    }
}