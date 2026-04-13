package com.example.unscramble.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "answer_history")

class AnswerHistoryRepository(private val context: Context) {

    companion object {
        private val HISTORY_KEY = stringPreferencesKey("correct_answers_history")
        private const val SEPARATOR = ","
    }

    // Flow that emits the list of correct answers from DataStore
    val correctAnswersHistory: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            val raw = preferences[HISTORY_KEY] ?: ""
            if (raw.isEmpty()) emptyList()
            else raw.split(SEPARATOR).filter { it.isNotEmpty() }
        }

    // Save a new correct answer to DataStore (appends to existing list)
    suspend fun addCorrectAnswer(word: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[HISTORY_KEY] ?: ""
            val newValue = if (current.isEmpty()) word else "$current$SEPARATOR$word"
            preferences[HISTORY_KEY] = newValue
        }
    }

    // Clear all history from DataStore
    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = ""
        }
    }
}