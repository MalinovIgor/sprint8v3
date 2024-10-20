package ru.startandroid.develop.sprint8v3.search.domain

import ru.startandroid.develop.sprint8v3.search.data.dto.Response

interface NetworkClient {
//    fun doRequest(dto: Any): Response
    suspend fun doRequest(dto: Any): Response
}