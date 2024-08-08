package ru.startandroid.develop.sprint8v3.ui.Search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Handler
import android.os.Looper
import ru.startandroid.develop.sprint8v3.Creator
import ru.startandroid.develop.sprint8v3.Observer
import ru.startandroid.develop.sprint8v3.R
import ru.startandroid.develop.sprint8v3.databinding.ActivitySearchBinding
import ru.startandroid.develop.sprint8v3.domain.api.HistoryInteractor
import ru.startandroid.develop.sprint8v3.domain.api.TracksInteractor
import ru.startandroid.develop.sprint8v3.domain.models.Track
import ru.startandroid.develop.sprint8v3.ui.player.PlayerActivity
import ru.startandroid.develop.sprint8v3.ui.player.selectedTrack

class SearchActivity : AppCompatActivity(), TrackAdapter.Listener, Observer {
    private val historyInteractor: HistoryInteractor by lazy { Creator.provideHistoryInteractor() }
    private lateinit var binding: ActivitySearchBinding
    private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter
    private val tracksInteractor: TracksInteractor by lazy { Creator.provideTracksInteractor() }
    private val searchRunnable = Runnable {
        search()
        hideSearchHistoryItems()
        update()
    }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    fun setupViews() {
        adapter = TrackAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        historyTracksToShow()
        showHistory()
    }

    fun setupOnCLickListeners() {
        binding.cleanHistory.setOnClickListener {
            historyInteractor.clearHistory()
            update()
        }

        binding.backFromSearch.setOnClickListener {
            finish()
        }

        binding.clearText.setOnClickListener {
            binding.editText.setText("")
            dataFromTextEdit = ""
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            hideErrorPlaceholder()
            binding.update.visibility = View.GONE
            historyTracksToShow()
            update()
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                hideSearchHistoryItems()
                searchTracksToShow(binding.editText.text.toString())
                tracks.clear()
                adapter.updateTracks(tracks)
                true
            } else {
                showHistory()
                false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupViews()
        setupOnCLickListeners()
        dataFromTextEdit = savedInstanceState?.getString(dataFromTextEditKey) ?: ""
        update()
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    hideErrorPlaceholder()
                    dataFromTextEdit = ""
                    binding.update.visibility = View.GONE
                    binding.clearText.visibility = View.INVISIBLE
                    historyTracksToShow()
                    update()
                } else {
                    binding.clearText.visibility = View.VISIBLE
                    dataFromTextEdit = s.toString()
                    historyTracksToShow()
                    showViewHolder()
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString(dataFromTextEditKey, dataFromTextEdit)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (::binding.isInitialized) {
            dataFromTextEdit = savedInstanceState.getString(dataFromTextEditKey) ?: ""
            binding.editText.setText(
                dataFromTextEdit)
        }
    }
    private fun hideSearchHistoryItems() {
        binding.cleanHistory.visibility = View.GONE
        binding.recentlyLookFor.visibility = View.GONE
        tracks.clear()
        historyTracksToShow()
    }

    private fun showErrorPlaceholder(text: Int, image: Int) {
        binding.progressBar.visibility = View.GONE
        tracks.clear()
        adapter.updateTracks(tracks)
        binding.recyclerView.visibility = View.GONE
        binding.placeholderMessage.setText(text)
        binding.placeholderMessage.visibility=View.VISIBLE
        binding.placeholderErrorImage.setImageResource(image)
        binding.placeholderErrorImage.visibility= View.VISIBLE
    }

    private fun hideErrorPlaceholder() {
        binding.placeholderMessage.visibility=View.GONE
        binding.placeholderErrorImage.visibility = View.GONE
    }

    private fun showViewHolder() {

        binding.progressBar.visibility = View.GONE
        binding.update.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.GONE
        binding.placeholderErrorImage.visibility = View.GONE
    }

    private fun search() {

        binding.progressBar.visibility = View.VISIBLE
        val query = binding.editText.text.toString()

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    handleSearchResponse(foundTracks)
                }
            }

            override fun onError(error: Throwable) {
                runOnUiThread {
                    showErrorPlaceholder(R.string.connection_trouble, R.drawable.connecton_trouble)
                    binding.update.visibility = View.VISIBLE
                    binding.update.setOnClickListener { searchDebounce() }
                }
            }
        })
    }

    private fun handleSearchResponse(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        if (tracks.isNotEmpty()) {
            this.tracks.clear()
            this.tracks.addAll(tracks)
            adapter.updateTracks(this.tracks)
            showViewHolder()
        } else {
            showErrorPlaceholder(R.string.nothing_found, R.drawable.nothings_found)
        }
    }

    private fun showHistory() {
        val updatedHistoryTracks = historyInteractor.loadHistoryTracks()
        binding.progressBar.visibility = View.GONE
        binding.cleanHistory.visibility = View.VISIBLE
        binding.recentlyLookFor.visibility = View.VISIBLE
        historyTracksToShow()
        adapter.updateTracks(updatedHistoryTracks)
    }

    override fun onClick(track: Track) {
        if (clickDebounce()) {
            historyInteractor.addToHistory(track)
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(selectedTrack, track)
            startActivity(intent)
        }
    }

    override fun update() {
        if (dataFromTextEdit.isNotEmpty()) {
            binding.recyclerView.adapter = adapter
            adapter.updateTracks(tracks)
            showViewHolder()
        } else {
            if (historyInteractor.isHistoryEmpty()) {
                hideSearchHistoryItems()
            } else {
                showHistory()
            }
        }
    }

    override fun onResume() {
        update()
        super.onResume()
    }

    private fun searchDebounce() {
        binding.progressBar.visibility = View.VISIBLE
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }


    fun historyTracksToShow() {
        adapter.updateTracks(historyInteractor.loadHistoryTracks())
    }

    fun searchTracksToShow(expression: String) {
        tracksInteractor.searchTracks(expression, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    adapter.updateTracks(foundTracks)
                }
            }

            override fun onError(error: Throwable) {
                runOnUiThread {
                }
            }
        })
    }

    companion object {
        lateinit var dataFromTextEdit: String
        private const val dataFromTextEditKey = "dataFromTextEdit"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}