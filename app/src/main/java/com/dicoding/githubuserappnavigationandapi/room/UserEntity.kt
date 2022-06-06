package com.dicoding.githubuserappnavigationandapi.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
class UserEntity(
    @field:ColumnInfo(name = "username")
    @field:PrimaryKey
    val username: String,

    @field:ColumnInfo(name = "avatar_url")
    val url: String
)