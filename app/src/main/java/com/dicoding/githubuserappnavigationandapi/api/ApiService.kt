package com.dicoding.githubuserappnavigationandapi.api

import com.dicoding.githubuserappnavigationandapi.BuildConfig
import com.dicoding.githubuserappnavigationandapi.response.DetailResponse
import com.dicoding.githubuserappnavigationandapi.response.SearchResponse
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    @Headers("Authorization: token $TOKEN")
    suspend fun loadUser(): ArrayList<UserItem>

    @GET("search/users")
    @Headers("Authorization: token $TOKEN")
    fun searchUser(
        @Query("q") q: String
    ): Call<SearchResponse>

    @GET("users/{username}")
    @Headers("Authorization: token $TOKEN")
    fun detailUser(
        @Path("username") username: String
    ): Call<DetailResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token $TOKEN")
    suspend fun userFollowers(
        @Path("username") username: String
    ): ArrayList<UserItem>

    @GET("users/{username}/following")
    @Headers("Authorization: token $TOKEN")
    suspend fun userFollowing(
        @Path("username") username: String
    ): ArrayList<UserItem>

    companion object {
        private const val TOKEN = BuildConfig.TOKEN
    }
}