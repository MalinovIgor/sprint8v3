package ru.startandroid.develop.sprint8v3.domain.repository

interface SettingsRepository {

    fun getThemePreference() : Boolean

    fun setThemePreference(darkThemeEnabled:Boolean)
}