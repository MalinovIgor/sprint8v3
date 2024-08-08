package ru.startandroid.develop.sprint8v3.domain.api

import ru.startandroid.develop.sprint8v3.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
        fun onError(error: Throwable)
    }
}