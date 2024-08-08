package ru.startandroid.develop.sprint8v3.domain.impl

import ru.startandroid.develop.sprint8v3.domain.api.SettingsInteractor
import ru.startandroid.develop.sprint8v3.domain.repository.SettingsRepository

class SettingsInteractorImpl (private val repository: SettingsRepository) : SettingsInteractor {
    override fun getThemePreference(): Boolean {
        return repository.getThemePreference()
    }

    override fun setThemePreference(darkThemeEnabled: Boolean) {
        repository.setThemePreference(darkThemeEnabled)
    }

}