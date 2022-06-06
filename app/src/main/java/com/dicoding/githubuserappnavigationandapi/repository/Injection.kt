package com.dicoding.githubuserappnavigationandapi.repository

import android.content.Context
import com.dicoding.githubuserappnavigationandapi.room.UserDatabase

object Injection {
    fun provideRepository(context: Context): DataRepository {
        val database = UserDatabase.getInstance(context)
        val dao = database.userDao()
        return DataRepository.getInstance(dao)
    }
}