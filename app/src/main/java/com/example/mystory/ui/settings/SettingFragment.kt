package com.example.mystory.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentSettingBinding
import com.example.mystory.factory.PrefViewModelFactory
import com.example.mystory.ui.viewmodel.PrefViewModel

class SettingFragment : Fragment() {
    private var _binding : FragmentSettingBinding? = null
    private val binding get() = _binding
    private val prefViewModel by lazy {
        val factory = PrefViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        ViewModelProvider(this,factory)[PrefViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefViewModel.getUser().observe(viewLifecycleOwner){ value ->
            if(value?.userId?.isEmpty() as Boolean){
                findNavController().navigate(R.id.action_navigation_setting_to_loginFragment)
            }
        }
        binding?.btnSettingLanguage?.setOnClickListener{
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding?.btnSettingLogout?.setOnClickListener {
            prefViewModel.signoutUser()
        }
    }
}