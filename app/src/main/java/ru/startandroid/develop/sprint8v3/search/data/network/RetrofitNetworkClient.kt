package ru.startandroid.develop.sprint8v3.search.data.network


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.startandroid.develop.sprint8v3.search.data.dto.ItunesResponse
import ru.startandroid.develop.sprint8v3.search.data.dto.Response
import ru.startandroid.develop.sprint8v3.search.data.dto.TracksSearchRequest
import ru.startandroid.develop.sprint8v3.search.domain.NetworkClient

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override suspend fun doRequest(dto: Any): Response {
//        return if (dto is TracksSearchRequest) {
//            val resp = itunesService.search(dto.expression)//.execute()

        //     val body = resp.body()
        //     if (body != null) {
//                ItunesResponse(body.results, resp.code())
//            } else {
//                ItunesResponse(emptyList(), resp.code())
//            }
//        } else {
//            Response(400)
//        }
//    }
        if (dto !is TracksSearchRequest) {
            return Response(400) }

        return withContext(Dispatchers.IO) {
            try {
                val resp =
                    itunesService.search(dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response(500)
            }
        }
    }
}