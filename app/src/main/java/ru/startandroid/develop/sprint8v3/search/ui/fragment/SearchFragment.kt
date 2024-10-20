package ru.startandroid.develop.sprint8v3.search.ui.fragment

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.text.intl.Locale
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

import kotlinx.coroutines.Job

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.startandroid.develop.sprint8v3.R
import ru.startandroid.develop.sprint8v3.databinding.FragmentSearchBinding
import ru.startandroid.develop.sprint8v3.player.ui.PlayerActivity
import ru.startandroid.develop.sprint8v3.player.ui.SELECTEDTRACK
import ru.startandroid.develop.sprint8v3.search.domain.models.Track
import ru.startandroid.develop.sprint8v3.search.ui.SearchActivityViewModel
import ru.startandroid.develop.sprint8v3.search.ui.SearchState
import ru.startandroid.develop.sprint8v3.search.ui.TrackAdapter

class SearchFragment : Fragment(), TrackAdapter.Listener {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModel<SearchActivityViewModel>()
    private var needLoadHistory: Boolean = true
    private lateinit var adapter: TrackAdapter
    var isClickAllowed = true
    private var searchJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        needLoadHistory = true
        viewModel.searchState.value?.let { renderState(it) }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupOnClickListeners()

        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        binding.editText.addTextChangedListener(onTextChanged = { s, _, _, _ ->
            if (s.isNullOrEmpty()) {
                hideErrorPlaceholder()
                viewModel.loadHistory()
                binding.clearText.isInvisible = true
                viewModel.onCleared()
                binding.editText.text.clear()
                searchJob?.cancel()

            } else {
                searchJob?.cancel()

                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(CLICK_DEBOUNCE_DELAY)
                    searchDebounce(s.toString())
                }
                binding.clearText.isVisible = true
            }
        }
        )

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > 200) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupViews() {
        adapter = TrackAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupOnClickListeners() {
        binding.cleanHistory.setOnClickListener {
            viewModel.clearHistory()
            viewModel.loadHistory()
        }

        binding.clearText.setOnClickListener {
            binding.editText.setText("")
            hideErrorPlaceholder()
            viewModel.loadHistory()
            showHistory()
        }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.editText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchDebounce(query)
                } else {
                    binding.editText.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.ContentHistoryTracks -> {
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
                needLoadHistory = true
            }

            is SearchState.Loading -> {
                showLoading(true)
            }

            is SearchState.NothingFound -> {
                hideSearchHistoryItems()
                showErrorPlaceholder(getString(R.string.nothing_found), R.drawable.nothings_found)
                needLoadHistory = true

            }
        }
    }

    private fun showHistory() {
        binding.progressBar.isGone = true
        binding.cleanHistory.isVisible = true
        binding.recentlyLookFor.isVisible = true
        if (needLoadHistory) {
            viewModel.loadHistory()
            binding.recyclerView.isVisible = true
            needLoadHistory = false
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

        if (isClickAllowed) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(SELECTEDTRACK, track)
            startActivity(intent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = true
        }

        viewModel.onClick(track)

    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2500L

    }
}