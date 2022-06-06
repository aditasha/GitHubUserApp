package com.dicoding.githubuserappnavigationandapi.ui.favorite

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.githubuserappnavigationandapi.repository.DataRepository
import com.dicoding.githubuserappnavigationandapi.repository.Injection

class FavoriteViewModelFactory private constructor(private val dataRepository: DataRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: FavoriteViewModelFactory? = null
        fun getInstance(context: Context): FavoriteViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: FavoriteViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}