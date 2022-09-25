package com.way.storyapp.data.local.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.way.storyapp.data.local.model.DataStoreRepository.Companion.PREFERENCES_KEY
import com.way.storyapp.data.local.model.DataStoreRepository.PreferencesKey.name
import com.way.storyapp.data.local.model.DataStoreRepository.PreferencesKey.stateLogin
import com.way.storyapp.data.local.model.DataStoreRepository.PreferencesKey.token
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_KEY)

class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private object PreferencesKey {
        val name = stringPreferencesKey(NAME_KEY)
        val stateLogin = booleanPreferencesKey(STATE_KEY)
        val token = stringPreferencesKey(TOKEN_KEY)
    }

    private val dataStore = context.dataStore

    fun readAccount(): Flow<UserModel> = dataStore.data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }.map { preferences ->
        val userModel = UserModel(
            preferences[name] ?: "",
            preferences[stateLogin] ?: false,
            preferences[token] ?: ""
        )
        userModel
    }

    suspend fun saveAccount(user: UserModel) = dataStore.edit { preferences ->
        preferences[name] = user.name
        preferences[stateLogin] = user.stateLogin
        preferences[token] = user.token
    }

    fun readToken(): Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }.map { preferences ->
        preferences[token] ?: ""
    }

    suspend fun login() = dataStore.edit { preferences ->
        preferences[stateLogin] = true
    }

    suspend fun logout() = dataStore.edit { preferences ->
        preferences.clear()
    }

    companion object {
        const val PREFERENCES_KEY = "auth_preferences"
        const val NAME_KEY = "name"
        const val STATE_KEY = "state"
        const val TOKEN_KEY = "token"
    }
}