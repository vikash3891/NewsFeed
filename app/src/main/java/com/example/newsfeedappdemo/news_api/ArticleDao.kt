package com.example.newsfeedappdemo.news_api

import androidx.room.*
import com.example.newsfeedappdemo.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(user: Article?)

    @get:Query("SELECT * FROM Article")
    val getAll: List<Article>

    @Delete
    fun delete(user: Article)

    @Query("DELETE FROM Article")
    fun deleteAll()
}