package com.example.mystory.ui.addstory

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentAddStoryBinding
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.ui.viewmodel.MainViewModel
import com.example.mystory.util.Util
import com.example.mystory.util.Util.bitmapToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStoryFragment : Fragment() {
    private var _binding : FragmentAddStoryBinding? = null
    private val binding get() = _binding
    private val mainViewModel : MainViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore))
    }
    private val loginViewModel : LoginViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore))
    }
    private var getFile : File? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddStoryBinding.inflate(layoutInflater,container,false)
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
        setupView()
    }

    private fun openGallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent,"Choose a picture")
        launchIntentGallery.launch(chooser)
    }

    private fun uploadImage(){
        if(getFile != null){
            val file = Util.reduceFileImage(getFile as File)
            val descriptionText = binding?.edAddStoryDescription?.text
            if(!descriptionText.isNullOrEmpty()){
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val image = MultipartBody.Part.createFormData("photo",file.name,requestImageFile)
                loginViewModel.getUser().observe(viewLifecycleOwner){ user ->
                    mainViewModel.addNewStory(user?.token.toString(),image,binding?.edAddStoryDescription?.text.toString())

                    mainViewModel.isLoading.observe(viewLifecycleOwner){ isBoolean ->
                        if(isBoolean) {
                            binding?.pbAddStory?.visibility = View.VISIBLE
                        } else {
                            binding?.pbAddStory?.visibility = View.GONE
                            findNavController().navigate(R.id.action_addStoryFragment_to_mainFragment)
                        }
                    }
                }
            }else Toast.makeText(requireContext(), getString(R.string.description_empty), Toast.LENGTH_SHORT).show()

        }else Toast.makeText(requireContext(),getString(R.string.file_empty),Toast.LENGTH_SHORT).show()
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK){
            val selectedImg : Uri = result.data?.data as Uri
            val myFile = Util.uriToFile(selectedImg,requireContext())
            getFile = myFile
            binding?.ipAddStoryImage?.setImageURI(selectedImg)
        }
    }

    private fun setupView(){
        val fileUri = arguments?.get("photo")
        if(fileUri != null){
            val uri : Uri = fileUri as Uri
            val isBackCamera = arguments?.getBoolean("isBackCamera") as Boolean
            val result = Util.rotateBitmap(
                BitmapFactory.decodeFile(uri.path),
                isBackCamera
            )
            getFile = result.bitmapToFile(requireContext())
            binding?.ipAddStoryImage?.setImageBitmap(result)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}