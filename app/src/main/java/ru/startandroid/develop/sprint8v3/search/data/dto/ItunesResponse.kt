package ru.startandroid.develop.sprint8v3.search.data.dto


class ItunesResponse (val results: List<TrackDto>, resultCode: Int) : Response(resultCode)