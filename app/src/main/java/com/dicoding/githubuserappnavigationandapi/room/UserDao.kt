package com.dicoding.githubuserappnavigationandapi.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM favorite ORDER BY username ASC")
    suspend fun getFavorite(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(user: UserEntity)

    @Query("DELETE FROM favorite WHERE username = :username")
    suspend fun deleteFavorite(username: String)

    @Query("SELECT * FROM favorite WHERE username = :username LIMIT 1")
    suspend fun getUser(username: String): List<UserEntity>
}