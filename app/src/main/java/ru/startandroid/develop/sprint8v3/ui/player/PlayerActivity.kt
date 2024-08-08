package ru.startandroid.develop.sprint8v3.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import android.os.Handler
import android.os.Looper
import android.util.Log
import ru.startandroid.develop.sprint8v3.R
import ru.startandroid.develop.sprint8v3.domain.models.Track

const val selectedTrack = "selectedTrack"
const val noData = "отсутствует"

class PlayerActivity : AppCompatActivity() {



    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = timer()

    private lateinit var playButton: ImageView
    private lateinit var timer: TextView

    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var releaseDate: TextView
    private lateinit var collectionName: TextView
    private lateinit var trackTime: TextView
    private lateinit var backFromPlayer: ImageView
    private lateinit var imageView: ImageView

    private val yearDateFormat by lazy { SimpleDateFormat("yyyy", Locale.getDefault()) }
    private val releaseDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    private val timerDateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        Log.e("PlayerActivity", selectedTrack.toString())

        setupViews()

        backFromPlayer.setOnClickListener {
            finish()
        }
        playButton.isEnabled = false
        val selectedTrack = intent.getSerializableExtra(selectedTrack) as Track

        loadTrackInfo(selectedTrack)
    }

    private fun setupViews() {
        backFromPlayer = findViewById(R.id.back_arrow)
        imageView = findViewById(R.id.artworkImageViewBig)
        trackNameTextView = findViewById(R.id.trackNameTextView)
        artistNameTextView = findViewById(R.id.artistNameTextView)
        primaryGenreName = findViewById(R.id.genreTextView)
        country = findViewById(R.id.countryTextView)
        releaseDate = findViewById(R.id.releaseDateTextView)
        collectionName = findViewById(R.id.collectionNameTextView)
        trackTime = findViewById(R.id.trackTimeTextView)
        timer = findViewById(R.id.timer)
        playButton = findViewById(R.id.play)
    }

    private fun loadTrackInfo(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .fitCenter()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.small_one)))
            .apply(RequestOptions().placeholder(R.drawable.placeholder_image))
            .into(imageView)

        preparePlayer(track.previewUrl.toString())
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        primaryGenreName.text = track.primaryGenreName ?: noData
        country.text = track.country ?: noData

        val releaseDateString = track.releaseDate ?: noData
        if (releaseDateString == noData) {
            releaseDate.text = releaseDateString
        } else {
            try {
                val date = releaseDateFormat.parse(releaseDateString)
                releaseDate.text = yearDateFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                releaseDate.text = noData
            }
        }

        collectionName.text = track.collectionName
        trackTime.text = timerDateFormat.format(track.trackTime)

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timerRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer(url: String?) {
        if (url == null) {
            return
        }
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play)
            timer.text = timerDateFormat.format(0)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(timerRunnable)
    }

    private fun timer(): Runnable {
        return Runnable {
            if (playerState == STATE_PLAYING) {
                timer.text = timerDateFormat.format(mediaPlayer.currentPosition)
                handler.postDelayed(timerRunnable, TIMER_UPDATE_DELAY)
            }
        }
    }


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_UPDATE_DELAY = 500L
    }
}