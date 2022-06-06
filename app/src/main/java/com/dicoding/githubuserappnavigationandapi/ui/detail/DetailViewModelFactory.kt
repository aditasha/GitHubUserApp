package com.dicoding.githubuserappnavigationandapi.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(private val user_id: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(user_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}