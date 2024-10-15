package ru.startandroid.develop.sprint8v3.search.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.startandroid.develop.sprint8v3.search.data.dto.ItunesResponse
import ru.startandroid.develop.sprint8v3.search.data.dto.TracksSearchRequest
import ru.startandroid.develop.sprint8v3.search.domain.NetworkClient
import ru.startandroid.develop.sprint8v3.search.domain.models.Resource
import ru.startandroid.develop.sprint8v3.search.domain.models.Track
import ru.startandroid.develop.sprint8v3.search.domain.repository.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            emit(Resource.success((response as ItunesResponse).results.map { dto ->
                Track(
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTime = dto.trackTime,
                    artworkUrl100 = dto.artworkUrl100,
                    trackId = dto.trackId,
                    collectionName = dto.collectionName,
                    releaseDate = dto.releaseDate,
                    primaryGenreName = dto.primaryGenreName,
                    country = dto.country,
                    previewUrl = dto.previewUrl
                )
            }))
        } else {
            emit(Resource.error<List<Track>>("Ошибка при получении данных: код ответа ${response.resultCode}"))
        }
    }.catch { error ->
        emit(Resource.error<List<Track>>(error.message ?: "Неизвестная ошибка"))
    }
}