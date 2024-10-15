package ru.startandroid.develop.sprint8v3.player.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.startandroid.develop.sprint8v3.player.domain.api.PlayerInteractor
import ru.startandroid.develop.sprint8v3.player.state.PlayerState
import ru.startandroid.develop.sprint8v3.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivityViewModel(private val interactor: PlayerInteractor) : ViewModel() {

    val playerState = MutableLiveData(PlayerState.STATE_DEFAULT)

    private var timerJob: Job? = null

    private val _currentTime = MutableLiveData<Int>()
    val currentTime: LiveData<Int> get() = _currentTime
    fun observePlayerState(): LiveData<PlayerState> = playerState

    init {
        prepare()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(300L)
                _currentTime.postValue(interactor.getCurrentTime())
            }
        }
    }

    fun play() {
        playerState.postValue(PlayerState.STATE_PLAYING)
        interactor.play()
        startTimer()
    }

    fun getState() {
        playerState.postValue(interactor.getState())
    }

    fun pause() {
        playerState.postValue(PlayerState.STATE_PAUSED)

        interactor.pause()
    }

    fun stop() {
        interactor.stop()
        playerState.postValue(PlayerState.STATE_STOPPED)
    }

    fun prepare() {
        interactor.prepare()
        playerState.postValue(PlayerState.STATE_PREPARED)
    }

    fun parseDate(releaseDateString: String?, noData: String): String {
        if (releaseDateString == noData || releaseDateString == null) {
            return noData
        } else {
            return try {
                val releaseDateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = releaseDateFormat.parse(releaseDateString)
                val outputDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
                outputDateFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                noData
            }
        }
    }
}