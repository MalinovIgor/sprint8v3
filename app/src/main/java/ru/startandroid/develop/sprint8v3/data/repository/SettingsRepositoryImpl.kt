package ru.startandroid.develop.sprint8v3.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import ru.startandroid.develop.sprint8v3.domain.repository.SettingsRepository
import ru.startandroid.develop.sprint8v3.ui.Settings.DARK_THEME

class SettingsRepositoryImpl (private val sharedPreferences: SharedPreferences) :SettingsRepository{
    override fun getThemePreference(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }

    override fun setThemePreference(darkThemeEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(DARK_THEME, darkThemeEnabled)
        editor.apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            })
    }
}