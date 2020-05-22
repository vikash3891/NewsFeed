package com.example.newsfeedappdemo.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Article")
data class Article(
    @Embedded(prefix = "source") val source: Source,
    @ColumnInfo(name = "author", defaultValue = "") val author: String,
    @ColumnInfo(name = "title", defaultValue = "") val title: String,
    @ColumnInfo(name = "description", defaultValue = "") val description: String,
    @ColumnInfo(name = "url", defaultValue = "") val url: String,
    @ColumnInfo(name = "urlToImage", defaultValue = "") val urlToImage: String,
    @ColumnInfo(name = "publishedAt", defaultValue = "") val publishedAt: String,
    @ColumnInfo(name = "content", defaultValue = "") val content: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}