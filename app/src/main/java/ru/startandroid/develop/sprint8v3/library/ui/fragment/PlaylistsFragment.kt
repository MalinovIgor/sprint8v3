package ru.startandroid.develop.sprint8v3.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.startandroid.develop.sprint8v3.databinding.FragmentPlaylistsBinding
import ru.startandroid.develop.sprint8v3.library.ui.PlaylistsViewModel

class PlaylistsFragment : Fragment() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel {
        parametersOf()
    }

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }




    companion object {
        private const val MOCK_KEY = "mockmock"

        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(MOCK_KEY, "1")
            }
        }
    }



}