package com.dicoding.githubuserappnavigationandapi.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.githubuserappnavigationandapi.api.ApiConfig
import com.dicoding.githubuserappnavigationandapi.response.SearchResponse
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.themeswitcher.SettingPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _listUser = MutableLiveData<ArrayList<UserItem>>()
    val listUser: LiveData<ArrayList<UserItem>> = _listUser

    private val _searchUser = MutableLiveData<ArrayList<UserItem>>()
    val searchUser: LiveData<ArrayList<UserItem>> = _searchUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed: LiveData<Boolean> = _isFailed

    private val _lastTry = MutableLiveData<String>()
    val lastTry: LiveData<String> = _lastTry

    private val _errorText = MutableLiveData<String>()
    val errorText: LiveData<String> = _errorText

    init {
        viewModelScope.launch {
            loadUser()
        }
    }

    suspend fun loadUser() {
        _isLoading.value = true
        try {
            val client = ApiConfig.getApiService().loadUser()
            _isLoading.value = false
            _listUser.postValue(client)
        } catch (e: Throwable) {
            Log.e(TAG, "onFailure: ${e.message.toString()}")
            _isLoading.value = false
            _lastTry.postValue("load_user")
            _errorText.postValue(e.message.toString())
            _isFailed.postValue(true)
        }
    }

    fun getUser(user_id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchUser(user_id)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _searchUser.postValue(response.body()?.items)
                } else {
                    Log.e(TAG, "onResponseFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
                _lastTry.postValue("get_user")
                _errorText.postValue(t.message.toString())
                _isFailed.postValue(true)
            }
        })
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