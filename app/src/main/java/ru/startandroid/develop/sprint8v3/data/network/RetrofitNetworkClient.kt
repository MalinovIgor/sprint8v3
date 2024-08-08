package ru.startandroid.develop.sprint8v3.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.startandroid.develop.sprint8v3.data.dto.ItunesResponse
import ru.startandroid.develop.sprint8v3.data.dto.Response
import ru.startandroid.develop.sprint8v3.data.dto.TracksSearchRequest
import ru.startandroid.develop.sprint8v3.domain.NetworkClient

class RetrofitNetworkClient: NetworkClient {

    private val itunesBaseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val resp = itunesService.search(dto.expression).execute()

            val body = resp.body() ?: ItunesResponse(emptyList())
            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}