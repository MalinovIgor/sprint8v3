package ru.startandroid.develop.sprint8v3.data.repository

import ru.startandroid.develop.sprint8v3.data.dto.ItunesResponse
import ru.startandroid.develop.sprint8v3.data.dto.TracksSearchRequest
import ru.startandroid.develop.sprint8v3.domain.NetworkClient
import ru.startandroid.develop.sprint8v3.domain.repository.TracksRepository
import ru.startandroid.develop.sprint8v3.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as ItunesResponse).results.map { dto ->
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
            }
        } else {
            return emptyList()
        }
    }
}