package com.dicoding.githubuserappnavigationandapi.ui.detail

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.githubuserappnavigationandapi.api.ApiConfig
import com.dicoding.githubuserappnavigationandapi.response.DetailResponse
import com.dicoding.githubuserappnavigationandapi.themeswitcher.SettingPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(user_id: String) : ViewModel() {
    private val _detailUser = MutableLiveData<DetailResponse>()
    val detailUser: LiveData<DetailResponse> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed: LiveData<Boolean> = _isFailed

    private val _errorText = MutableLiveData<String>()
    val errorText: LiveData<String> = _errorText

    init {
        detailUser(user_id)
    }

    fun detailUser(user_id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().detailUser(user_id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailUser.postValue(response.body())
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
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