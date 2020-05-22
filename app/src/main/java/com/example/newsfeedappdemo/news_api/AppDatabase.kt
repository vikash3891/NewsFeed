package com.example.newsfeedappdemo.news_api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsfeedappdemo.model.Article

@Database(
    entities = [Article::class],
    version = 4, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mDao(): ArticleDao

    companion object {

        /**
         * The only instance
         */
        private var sInstance: AppDatabase? = null

        /**
         * Gets the singleton instance of SampleDatabase.
         *
         * @param context The context.
         * @return The singleton instance of SampleDatabase.
         */
        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "article.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return sInstance!!
        }
    }

}