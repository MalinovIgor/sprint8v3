package ru.startandroid.develop.sprint8v3.search.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.startandroid.develop.sprint8v3.search.data.dto.ItunesResponse


const val termForGet = "term"
interface ItunesAPI {
    @GET("/search?entity=song ")

    suspend fun search(@Query(termForGet) text: String) : ItunesResponse
}