package ru.startandroid.develop.sprint8v3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import ru.startandroid.develop.sprint8v3.Creator.initApplication

const val DARK_THEME = "dark_theme"
const val USER_PREFERENCES = "user_preferences"

class App : Application() {
    init {   instance = this
    }
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        val sharedPrefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
        val isDarkTheme = sharedPrefs.getBoolean(DARK_THEME, false)

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val sharedPrefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean(DARK_THEME, darkThemeEnabled)
        editor.apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        lateinit var instance: App
            private set
    }
}