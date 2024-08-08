package ru.startandroid.develop.sprint8v3.domain.impl

import ru.startandroid.develop.sprint8v3.domain.api.TracksInteractor
import ru.startandroid.develop.sprint8v3.domain.repository.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(expression)
                consumer.consume(tracks)
            } catch (e: Exception) {
                consumer.onError(e)
            }
        }
    }
}