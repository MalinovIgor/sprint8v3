package ru.startandroid.develop.sprint8v3.ui.Settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ru.startandroid.develop.sprint8v3.Creator

const val DARK_THEME = "dark_theme"
const val USER_PREFERENCES = "user_preferences"

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)

    val settingsInteractor = Creator.provideSettingsInteractor()
    val isDarkTheme = settingsInteractor.getThemePreference()
    if (isDarkTheme) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}}