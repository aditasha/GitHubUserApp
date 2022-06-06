package com.dicoding.githubuserappnavigationandapi.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SearchResponse(

    @field:SerializedName("items")
    val items: ArrayList<UserItem>

)

@Parcelize
data class UserItem(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("avatar_url")
    val avatarUrl: String

) : Parcelable

@Parcelize
data class DetailResponse(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("company")
    val company: String,

    @field:SerializedName("public_repos")
    val publicRepos: Int,

    @field:SerializedName("followers_url")
    val followersUrl: String,

    @field:SerializedName("url")
    val url: String,

    @field:SerializedName("followers")
    val followers: Int,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    @field:SerializedName("following")
    val following: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("location")
    val location: String

) : Parcelable
