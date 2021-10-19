package com.example.ktshw1.db

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query

import kotlinx.coroutines.flow.Flow


@Dao
interface SubredditDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<SubredditT>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItem(items: SubredditT)

    @Query("SELECT * " +
            "FROM ${SubredditContract.TABLE_NAME} " +
            "ORDER BY ${SubredditContract.Columns.CREATED} DESC")
    fun observeSubreddits(): Flow<List<SubredditT>>

}
