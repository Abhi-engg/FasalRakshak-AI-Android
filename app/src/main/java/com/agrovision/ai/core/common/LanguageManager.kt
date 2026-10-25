package com.agrovision.ai.core.common

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLanguage(val code: String, val displayName: String, val shortLabel: String) {
    HINDI("hi", "हिन्दी (Hindi)", "हिन्दी"),
    MARATHI("mr", "मराठी (Marathi)", "मराठी"),
    ENGLISH("en", "English", "EN")
}

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var currentLanguage by mutableStateOf(loadSavedLanguage())
        private set

    private fun loadSavedLanguage(): AppLanguage {
        val saved = prefs.getString("selected_lang", AppLanguage.HINDI.code)
        return AppLanguage.entries.find { it.code == saved } ?: AppLanguage.HINDI
    }

    fun setLanguage(lang: AppLanguage) {
        currentLanguage = lang
        prefs.edit().putString("selected_lang", lang.code).apply()
    }
}
