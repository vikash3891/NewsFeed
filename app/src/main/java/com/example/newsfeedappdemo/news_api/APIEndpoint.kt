package com.example.newsfeedappdemo.news_api

import com.example.newsfeedappdemo.model.TopHeadlines
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface APIEndpoint {

    //News List API
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Observable<TopHeadlines>


    //Search API
    @GET("top-headlines")
    fun getUserSearchInput(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String
    ): Observable<TopHeadlines>
}