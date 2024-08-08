package ru.startandroid.develop.sprint8v3

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import ru.startandroid.develop.sprint8v3.data.repository.SearchHistoryRepositoryImpl
import ru.startandroid.develop.sprint8v3.data.repository.TracksRepositoryImpl
import ru.startandroid.develop.sprint8v3.data.network.RetrofitNetworkClient
import ru.startandroid.develop.sprint8v3.data.repository.SettingsRepositoryImpl
import ru.startandroid.develop.sprint8v3.domain.api.HistoryInteractor
import ru.startandroid.develop.sprint8v3.domain.api.SettingsInteractor
import ru.startandroid.develop.sprint8v3.domain.api.TracksInteractor
import ru.startandroid.develop.sprint8v3.domain.impl.HistoryInteractorImpl
import ru.startandroid.develop.sprint8v3.domain.impl.SettingsInteractorImpl
import ru.startandroid.develop.sprint8v3.domain.repository.TracksRepository
import ru.startandroid.develop.sprint8v3.domain.impl.TracksInteractorImpl
import ru.startandroid.develop.sprint8v3.domain.repository.SearchHistoryRepository
import ru.startandroid.develop.sprint8v3.domain.repository.SettingsRepository
import ru.startandroid.develop.sprint8v3.ui.Settings.USER_PREFERENCES

object Creator {
    private lateinit var application: Application
    private const val HISTORY_NAME = "history_name"

    fun initApplication(application: Application) {
        this.application = application
    }

    fun provideSettingsRepository() : SettingsRepository {
        return SettingsRepositoryImpl(application.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE))
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    private fun provideSharedPreferences(key: String): SharedPreferences {
        return application.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun provideHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences(HISTORY_NAME))
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(provideHistoryRepository())
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

}