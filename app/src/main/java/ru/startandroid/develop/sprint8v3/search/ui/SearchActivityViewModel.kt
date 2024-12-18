package ru.startandroid.develop.sprint8v3.search.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.startandroid.develop.sprint8v3.R
import ru.startandroid.develop.sprint8v3.library.domain.api.FavoritesInteractor
import ru.startandroid.develop.sprint8v3.search.domain.api.HistoryInteractor
import ru.startandroid.develop.sprint8v3.search.domain.api.TracksInteractor
import ru.startandroid.develop.sprint8v3.search.domain.models.Resource
import ru.startandroid.develop.sprint8v3.search.domain.models.Track

class SearchActivityViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
    private val searchHistorySaver: HistoryInteractor
) :
    AndroidViewModel(application) {
    private var lastSearchedText: String? = null
    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState
    private var textChangedSearchDebounceJob: Job? = null
    private var debounceJob: Job? = null
    var isClickAllowed = false


    init {

        viewModelScope.launch {
            searchHistorySaver.loadHistoryTracks().collect { tracks ->
                if (tracks.isEmpty()) {
                    renderState(SearchState.NoTracks)
                } else {
                    renderState(SearchState.ContentHistoryTracks(tracks))
                }
            }
        }
    }

    public override fun onCleared() {
        textChangedSearchDebounceJob?.cancel()
    }

    private fun processResult(result: Resource<List<Track>>) {

        when (result) {
            is Resource.Error -> {
                renderState(
                    SearchState.Error(
                        errorMessage = getApplication<Application>().getString(R.string.connection_trouble)
                    )
                )
            }

            is Resource.Success -> {
                val foundTracks = result.data
                if (!foundTracks.isNullOrEmpty()) {
                    renderState(SearchState.ContentFoundTracks(foundTracks))
                } else {
                    renderState(SearchState.NothingFound)
                }
            }
        }
    }

    fun search(query: String) {
        renderState(SearchState.Loading)
        viewModelScope.launch {
            tracksInteractor
                .searchTracks(query)
                .collect { pair ->
                    processResult(pair)
                }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
        val historyTracks = searchHistorySaver.loadHistoryTracks().collect{tracks ->
        if (tracks.isEmpty()) {
            renderState(SearchState.NoTracks)
        } else {
            renderState(SearchState.ContentHistoryTracks(tracks))
        }
    }}
    }

    fun clearHistory() {
        searchHistorySaver.clearHistory()
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchedText == changedText) {
            return
        }
        lastSearchedText = changedText
        search(changedText)
    }

fun cleanLastSearchedText(){
    lastSearchedText=null
}

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }

    suspend fun onClick(track: Track) {
        searchHistorySaver.addToHistory(track)
        loadHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2500L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
