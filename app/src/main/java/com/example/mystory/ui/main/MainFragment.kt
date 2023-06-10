package com.example.mystory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mystory.R
import com.example.mystory.adapter.MainAdapter
import com.example.mystory.adapter.StoryLoadingStateAdapter
import com.example.mystory.data.Result
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentMainBinding
import com.example.mystory.factory.PrefViewModelFactory
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.MainViewModel
import com.example.mystory.ui.viewmodel.PrefViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding
    private lateinit var factory: ViewModelFactory
    private lateinit var prefFactory: PrefViewModelFactory
    private var mainViewModel: MainViewModel? = null
    private val prefViewModel: PrefViewModel by viewModels { prefFactory }
    private val mainAdapter = MainAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        factory = ViewModelFactory.getInstance(requireContext())
        mainViewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]
        prefFactory =
            PrefViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        setupView()
        setupList()
        binding?.fabMainAdd?.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addStoryFragment)
        }
    }

    private fun setupList() {
        binding?.rvMainList?.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            mainAdapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Int) {
                    scrollToPosition(data)
                }
            })
            adapter = mainAdapter.withLoadStateFooter(
                footer = StoryLoadingStateAdapter {
                    mainAdapter.retry()
                }
            )
            setupNoData(mainAdapter)
            scrollToPosition(0)

            mainAdapter.addLoadStateListener {
                mainViewModel?.dataResult(it)?.observe(requireActivity()) { state ->
                    when (state) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> showLoading(false)
                        is Result.Error -> showLoading(false)
                        is Result.ServerError -> showSnackbar(state.message)
                        is Result.Authorized -> showSnackbar(state.message)
                    }
                }
            }
        }
    }

    private fun setupNoData(mainAdapter: MainAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainAdapter.loadStateFlow
                .collectLatest {
                    if (it.prepend is LoadState.NotLoading && it.prepend.endOfPaginationReached) {
                        binding?.tvNodata?.isVisible = mainAdapter.itemCount < 1
                        binding?.imgNodata?.isVisible = mainAdapter.itemCount < 1
                    }
                }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pbMain?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        prefViewModel.getUser().observe(viewLifecycleOwner) { result ->
            if (result.userId.isEmpty()) {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            } else {
                mainViewModel?.getListStory(result.token)?.observe(viewLifecycleOwner) { list ->
                    mainAdapter.submitData(lifecycle, list)
                }
            }

        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(view as View, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
