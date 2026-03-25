package com.groze.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "groze_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    private val CURRENCY = stringPreferencesKey("currency")
    private val DARK_MODE = stringPreferencesKey("dark_mode")

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HAS_SEEN_ONBOARDING] ?: false
    }

    val currency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENCY] ?: "USD"
    }

    val darkMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE] ?: "system"
    }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { prefs ->
            prefs[HAS_SEEN_ONBOARDING] = true
        }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENCY] = currency
        }
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = mode
        }
    }
}
