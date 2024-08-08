package ru.startandroid.develop.sprint8v3.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.startandroid.develop.sprint8v3.data.dto.ItunesResponse


const val termForGet = "term"
interface ItunesAPI {
    @GET("/search?entity=song ")
    fun search(@Query(termForGet) text: String) : Call <ItunesResponse>
}