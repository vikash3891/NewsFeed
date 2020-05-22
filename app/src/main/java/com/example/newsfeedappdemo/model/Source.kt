package com.example.newsfeedappdemo.model

import androidx.room.ColumnInfo

data class Source(@ColumnInfo(name = "id") val id: String, @ColumnInfo(name = "name") val name: String)