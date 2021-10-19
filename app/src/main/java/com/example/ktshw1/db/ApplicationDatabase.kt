package com.example.ktshw1.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SubredditT::class,
    ], version = ApplicationDatabase.DB_VERSION
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun feedItemDao(): SubredditDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "app-database"
    }
}
