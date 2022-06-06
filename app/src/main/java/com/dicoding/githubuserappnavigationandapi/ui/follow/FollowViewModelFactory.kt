package com.dicoding.githubuserappnavigationandapi.ui.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class FollowViewModelFactory(private val user_id: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowViewModel::class.java)) {
            return FollowViewModel(user_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}