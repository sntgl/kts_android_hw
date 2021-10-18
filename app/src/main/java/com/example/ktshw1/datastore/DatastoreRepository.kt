package com.example.ktshw1.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import timber.log.Timber

interface DatastoreRepositoryInterface{
    suspend fun passOnBoarding()
    fun isOnBoardingPassed(): Flow<Boolean?>
    suspend fun redditTokenReceived(token: String)
    fun getRedditToken(): Flow<String?>

}

class DatastoreRepository(
    context: Context
): DatastoreRepositoryInterface {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

    private val dataStore: DataStore<Preferences> = context.dataStore

    override suspend fun passOnBoarding() {
        dataStore.edit { it[ONBOARDING_PASSED] = true }
    }
    override fun isOnBoardingPassed(): Flow<Boolean?> = dataStore.data.map { it[ONBOARDING_PASSED] }

    override suspend fun redditTokenReceived(token: String) {
        dataStore.edit { it[REDDIT_API_KEY] = token }
    }
    override fun getRedditToken(): Flow<String?> = dataStore.data.map { it[REDDIT_API_KEY] }


    companion object {
        private const val DATASTORE_NAME = "datastore"
        private val ONBOARDING_PASSED = booleanPreferencesKey("ONBOARDING_PASSED")
        private val REDDIT_API_KEY = stringPreferencesKey("REDDIT_API_KEY")
    }

}

