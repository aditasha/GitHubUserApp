package com.dicoding.githubuserappnavigationandapi.ui.favorite

import androidx.lifecycle.*
import com.dicoding.githubuserappnavigationandapi.repository.DataRepository
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.themeswitcher.SettingPreferences
import kotlinx.coroutines.launch

class FavoriteViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _favorite = MutableLiveData<ArrayList<UserItem>>()
    val favorite: LiveData<ArrayList<UserItem>> = _favorite

    private val _userFound = MutableLiveData<Boolean>()
    val userFound: LiveData<Boolean> = _userFound

    init {
        getFavorite()
    }

    private fun getFavorite() {
        viewModelScope.launch {
            _favorite.postValue(dataRepository.getFavorite())
        }
    }

    fun addFavorite(userItem: UserItem) {
        viewModelScope.launch {
            dataRepository.addFavorite(userItem)
        }
        getFavorite()
    }

    fun deleteFavorite(username: String) {
        viewModelScope.launch {
            dataRepository.deleteFavorite(username)
        }
        getFavorite()
    }

    fun getUser(username: String) {
        viewModelScope.launch {
            _userFound.postValue(dataRepository.getUser(username))
        }
    }

    fun getTheme(pref: SettingPreferences): LiveData<Boolean> {
        return pref.getTheme().asLiveData()
    }

    fun saveTheme(pref: SettingPreferences, darkMode: Boolean) {
        viewModelScope.launch {
            pref.saveTheme(darkMode)
        }
    }
}