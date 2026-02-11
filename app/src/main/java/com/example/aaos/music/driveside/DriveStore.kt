package com.example.aaos.music.driveside


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


object DriveStore {


    private val Context.dataStore by preferencesDataStore(name = "lhd_prefs")
    private val KEY_IS_LHD = booleanPreferencesKey("is_lhd")


    suspend fun read(context: Context): Boolean? =
        context.dataStore.data.map { it[KEY_IS_LHD] }.first()

    suspend fun write(value: Boolean) {
        // You may pass application context to avoid leaks
        appContext.dataStore.edit { it[KEY_IS_LHD] = value }
    }

    // Provide appContext from your Application class
    lateinit var appContext: Context
}
