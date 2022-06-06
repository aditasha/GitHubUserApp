package com.dicoding.githubuserappnavigationandapi.repository

import com.dicoding.githubuserappnavigationandapi.response.UserItem
import com.dicoding.githubuserappnavigationandapi.room.UserDao
import com.dicoding.githubuserappnavigationandapi.room.UserEntity

class DataRepository private constructor(
    private val userDao: UserDao
) {
    suspend fun getFavorite(): ArrayList<UserItem> {
        val list = userDao.getFavorite()
        val listUserItem = list.map {
            UserItem(
                it.username,
                it.url
            )
        }
        return ArrayList(listUserItem)
    }

    suspend fun addFavorite(user: UserItem) {
        val userList = UserEntity(user.login, user.avatarUrl)
        userDao.addFavorite(userList)
    }

    suspend fun deleteFavorite(username: String) {
        userDao.deleteFavorite(username)
    }

    suspend fun getUser(username: String): Boolean {
        val user = userDao.getUser(username)
        return user.isNotEmpty()
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null
        fun getInstance(
            userDao: UserDao
        ): DataRepository =
            instance ?: synchronized(this) {
                instance ?: DataRepository(userDao)
            }.also { instance = it }
    }
}