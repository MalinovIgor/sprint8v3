package ru.startandroid.develop.sprint8v3.search.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.startandroid.develop.sprint8v3.search.domain.models.Resource
import ru.startandroid.develop.sprint8v3.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}