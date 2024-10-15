package ru.startandroid.develop.sprint8v3.search.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.startandroid.develop.sprint8v3.search.domain.api.TracksInteractor
import ru.startandroid.develop.sprint8v3.search.domain.models.Resource
import ru.startandroid.develop.sprint8v3.search.domain.models.Track
import ru.startandroid.develop.sprint8v3.search.domain.repository.TracksRepository
class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(expression)
    }
}