package ru.startandroid.develop.sprint8v3.domain.repository

import ru.startandroid.develop.sprint8v3.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}