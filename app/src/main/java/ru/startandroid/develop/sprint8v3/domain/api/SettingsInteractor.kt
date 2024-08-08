package ru.startandroid.develop.sprint8v3.domain.api

interface SettingsInteractor {
    fun getThemePreference() : Boolean

    fun setThemePreference(darkThemeEnabled:Boolean)
}