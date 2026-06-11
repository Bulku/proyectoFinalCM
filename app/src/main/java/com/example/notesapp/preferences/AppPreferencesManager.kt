package com.example.notesapp.preferences

import android.content.Context
import com.example.notesapp.ui.theme.AppColorOption
import com.example.notesapp.ui.theme.AppFontOption

class AppPreferencesManager(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getColorOption(): AppColorOption {
        val storedValue = preferences.getString(KEY_COLOR_OPTION, AppColorOption.BLUE.name)
        return runCatching { AppColorOption.valueOf(storedValue ?: AppColorOption.BLUE.name) }
            .getOrDefault(AppColorOption.BLUE)
    }

    fun getFontOption(): AppFontOption {
        val storedValue = preferences.getString(KEY_FONT_OPTION, AppFontOption.DEFAULT.name)
        return runCatching { AppFontOption.valueOf(storedValue ?: AppFontOption.DEFAULT.name) }
            .getOrDefault(AppFontOption.DEFAULT)
    }

    fun saveColorOption(option: AppColorOption) {
        preferences.edit().putString(KEY_COLOR_OPTION, option.name).apply()
    }

    fun saveFontOption(option: AppFontOption) {
        preferences.edit().putString(KEY_FONT_OPTION, option.name).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "notes_app_preferences"
        private const val KEY_COLOR_OPTION = "color_option"
        private const val KEY_FONT_OPTION = "font_option"
    }
}
