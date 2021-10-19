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

    @Query("SELECT COUNT(*) FROM ${SubredditContract.TABLE_NAME}")
    fun observeCount(): Flow<Int>

}
