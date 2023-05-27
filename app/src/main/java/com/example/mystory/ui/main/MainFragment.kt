package com.example.mystory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mystory.R
import com.example.mystory.adapter.MainAdapter
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentMainBinding
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.ui.viewmodel.MainViewModel

class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding
    private lateinit var loginViewModel : LoginViewModel
    private lateinit var mainViewModel : MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupList()
        binding?.fabMainAdd?.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_loginFragment)
        }
    }

    private fun setupList(){
        mainViewModel.storyList.observe(viewLifecycleOwner){ list ->
            val mainAdapter = MainAdapter(list)
            binding?.rvMainList?.apply{
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = mainAdapter
            }
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            binding?.pbMain?.visibility = if(isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupView(){
        mainViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        )[MainViewModel::class.java]
        loginViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        )[LoginViewModel::class.java]
        loginViewModel.getUser().observe(viewLifecycleOwner){ value ->
            if(value?.userId?.isEmpty() as Boolean){
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            }else{
                mainViewModel.getListStory(value.token,requireContext())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}