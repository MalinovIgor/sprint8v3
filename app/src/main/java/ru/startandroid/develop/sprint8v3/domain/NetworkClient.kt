package ru.startandroid.develop.sprint8v3.domain

import ru.startandroid.develop.sprint8v3.data.dto.Response

interface NetworkClient { fun doRequest(dto: Any): Response
}