package com.example.ktshw1.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.ktshw1.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


class DatastoreRepository(
    context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun passOnBoarding() {
        dataStore.edit { it[ONBOARDING_PASSED] = true }
    }
    fun isOnBoardingPassed(): Flow<Boolean?> = dataStore.data.map { it[ONBOARDING_PASSED] }

    suspend fun redditTokenReceived(token: String?, refresh: String?, expires: Long?) {
        Timber.d("Received keychain:\nToken - $token expires $expires\nRefresh = $refresh")
        dataStore.edit {
            if (token != null) it[REDDIT_API_KEY] = token
            if (refresh != null) it[REDDIT_REFRESH_KEY] = refresh
            if (expires != null) it[REDDIT_EXPIRES_KEY] = expires
        }
    }

    fun getRedditToken(): Flow<String?> = dataStore.data.onEach{
        if (it[REDDIT_API_KEY] != null) UserInfo.authToken = it[REDDIT_API_KEY]
        if (it[REDDIT_REFRESH_KEY] != null) UserInfo.refreshToken = it[REDDIT_REFRESH_KEY]
        if (it[REDDIT_EXPIRES_KEY] != null) UserInfo.expires = it[REDDIT_EXPIRES_KEY]
//        Timber.d("Keychain given.\napi = ${it[REDDIT_API_KEY]}\nrefresh = ${it[REDDIT_REFRESH_KEY]}\nexpires = ${it[REDDIT_EXPIRES_KEY]}")
    }.map { it[REDDIT_API_KEY] }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }


    companion object {
        private const val DATASTORE_NAME = "datastore"
        private val ONBOARDING_PASSED = booleanPreferencesKey("ONBOARDING_PASSED")
        private val REDDIT_API_KEY = stringPreferencesKey("REDDIT_API_KEY")
        private val REDDIT_REFRESH_KEY = stringPreferencesKey("REDDIT_REFRESH_KEY")
        private val REDDIT_EXPIRES_KEY = longPreferencesKey("REDDIT_EXPIRES_KEY")
    }

}

