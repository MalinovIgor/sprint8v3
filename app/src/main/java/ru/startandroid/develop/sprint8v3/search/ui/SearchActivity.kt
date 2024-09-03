package ru.startandroid.develop.sprint8v3.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.startandroid.develop.sprint8v3.Creator
import ru.startandroid.develop.sprint8v3.Observer
import ru.startandroid.develop.sprint8v3.R
import ru.startandroid.develop.sprint8v3.databinding.ActivitySearchBinding
import ru.startandroid.develop.sprint8v3.player.ui.PlayerActivity
import ru.startandroid.develop.sprint8v3.player.ui.SELECTEDTRACK
import ru.startandroid.develop.sprint8v3.search.domain.models.Track
import ru.startandroid.develop.sprint8v3.search.ui.SearchState.ContentHistoryTracks

class SearchActivity : AppCompatActivity(), TrackAdapter.Listener, Observer {

    private val viewModel: SearchActivityViewModel by lazy {
        ViewModelProvider(
            this,
            SearchActivityViewModel.getViewModelFactory()
        )[SearchActivityViewModel::class.java]
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private var needLoadHistory: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupOnClickListeners()

        viewModel.searchState.observe(this) { state ->
            renderState(state)
        }

        binding.editText.addTextChangedListener( onTextChanged = {s, _, _, _ ->
            if (s.isNullOrEmpty()) {
                hideErrorPlaceholder()
                viewModel.loadHistory()
                binding.clearText.isInvisible = true
            } else {
                searchDebounce(s.toString())
                binding.clearText.isVisible = true
            }
        }
        )
    }

    private fun setupViews() {
        adapter = TrackAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupOnClickListeners() {
        binding.cleanHistory.setOnClickListener {
            viewModel.clearHistory()
            viewModel.loadHistory()
        }

        binding.backFromSearch.setOnClickListener {
            finish()
        }

        binding.clearText.setOnClickListener {
            binding.editText.setText("")
            hideErrorPlaceholder()
            viewModel.loadHistory()
            showHistory()
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce(binding.editText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is ContentHistoryTracks -> {
                showHistory()
                adapter.updateTracks(state.historyTracks)
                hideErrorPlaceholder()
                needLoadHistory = false
            }

            is SearchState.ContentFoundTracks -> {
                hideSearchHistoryItems()
                showViewHolder()
                adapter.updateTracks(state.tracks)
                hideErrorPlaceholder()
            }

            is SearchState.NoTracks -> {
                hideSearchHistoryItems()
                hideErrorPlaceholder()
                hideFoundItems()
                showLoading(false)
            }

            is SearchState.Error -> {
                showErrorPlaceholder(state.errorMessage, R.drawable.connecton_trouble)
                hideSearchHistoryItems()
            }

            is SearchState.Loading -> {
                showLoading(true)
            }

            is SearchState.NothingFound -> {
                hideSearchHistoryItems()
                showErrorPlaceholder(getString(R.string.nothing_found), R.drawable.nothings_found)
            }
        }
    }

    private fun showHistory() {
        binding.progressBar.isGone = true
        binding.cleanHistory.isVisible = true
        binding.recentlyLookFor.isVisible = true
        if (needLoadHistory) {
            viewModel.loadHistory()
        } else {
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun showViewHolder() {
        binding.progressBar.isGone = true
        binding.recyclerView.isVisible = true
        binding.cleanHistory.isGone = true
        binding.recentlyLookFor.isGone = true
    }

    private fun hideSearchHistoryItems() {
        binding.cleanHistory.isGone = true
        binding.recentlyLookFor.isGone = true
    }

    private fun showErrorPlaceholder(text: String, image: Int) {
        binding.progressBar.isGone = true
        binding.recyclerView.isGone = true
        binding.placeholderMessage.setText(text)
        binding.placeholderMessage.isVisible = true
        binding.placeholderErrorImage.setImageResource(image)
        binding.placeholderErrorImage.isVisible = true
    }

    private fun hideFoundItems() {
        binding.recyclerView.isGone = true
    }

    private fun hideErrorPlaceholder() {
        binding.placeholderMessage.isGone = true
        binding.placeholderErrorImage.isGone = true
    }

    private fun searchDebounce(query: String) {
        viewModel.searchDebounce(query)
    }

    override fun onClick(track: Track) {
        if (clickDebounce()) {
            Creator.provideHistoryInteractor().addToHistory(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(SELECTEDTRACK, track)
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun update() {
    }
}