package com.example.mystory.ui.addstory

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.data.Result
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentAddStoryBinding
import com.example.mystory.factory.PrefViewModelFactory
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.AddViewModel
import com.example.mystory.ui.viewmodel.PrefViewModel
import com.example.mystory.util.Util
import com.example.mystory.util.Util.bitmapToFile
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding
    private val addViewModel : AddViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var token: String
    private lateinit var prefViewModel: PrefViewModel
    private var getFile: File? = null
    private var lat = 0F
    private var lon = 0F


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddStoryBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.imbDetailBack?.setOnClickListener {
            findNavController().navigate(R.id.action_addStoryFragment_to_mainFragment)
        }

        binding?.btnAddStorySimpan?.setOnClickListener {
            uploadImage()
        }

        binding?.btnAddStoryKamera?.setOnClickListener {
            findNavController().navigate(R.id.action_addStoryFragment_to_cameraFragment)
        }

        binding?.btnAddStoryGaleri?.setOnClickListener {
            openGallery()
        }
        prefViewModel()
        setupView()
    }

    private fun prefViewModel() {
        prefViewModel = ViewModelProvider(
            requireActivity(),
            PrefViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        )[PrefViewModel::class.java]
        prefViewModel.getUser().observe(viewLifecycleOwner) {
            token = it.token
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launchIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = Util.reduceFileImage(getFile as File)
            val descriptionText = binding?.edAddStoryDescription?.text
            if (!descriptionText.isNullOrEmpty()) {
                addViewModel.getCurrentLocation().observe(viewLifecycleOwner) { result ->
                    if (binding?.cbAddStory?.isChecked as Boolean) {
                        lon = result.longitude.toFloat()
                        lat = result.latitude.toFloat()

                    }
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                    val description =
                        descriptionText.toString().toRequestBody("text/plain".toMediaType())
                    val image =
                        MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
                    addViewModel.addStory(
                        token,
                        image,
                        description,
                        lat,
                        lat
                    ).observe(viewLifecycleOwner) {
                        when (it) {
                            is Result.Loading -> showLoading(true)
                            is Result.Success -> {
                                showLoading(false)
                                showSnackbar(it.data.message)
                                findNavController().navigate(R.id.action_addStoryFragment_to_mainFragment)
                            }

                            is Result.Error -> {
                                showLoading(false)
                                showSnackbar(it.message)
                            }

                            is Result.Authorized, is Result.ServerError -> showSnackbar(it.toString())
                        }
                    }
                }
            } else showSnackbar(getString(R.string.description_empty))

        } else showSnackbar(getString(R.string.file_empty))
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Util.uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding?.ipAddStoryImage?.setImageURI(selectedImg)
        }
    }

    private fun setupView() {
        val fileUri = arguments?.get("photo")
        if (fileUri != null) {
            val uri: Uri = fileUri as Uri
            val isBackCamera = arguments?.getBoolean("isBackCamera") as Boolean
            val result = Util.rotateBitmap(
                BitmapFactory.decodeFile(uri.path),
                isBackCamera
            )
            getFile = result.bitmapToFile(requireContext())
            binding?.ipAddStoryImage?.setImageBitmap(result)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(view as View, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pbAddStory?.isVisible = isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}