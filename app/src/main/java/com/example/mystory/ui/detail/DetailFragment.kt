package com.example.mystory.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mystory.R
import com.example.mystory.databinding.FragmentDetailBinding
import com.example.mystory.util.Util
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)

class DetailFragment : Fragment() {
    private var _binding : FragmentDetailBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.imbDetailBack?.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_mainFragment)
        }
        setupView()
    }

    private fun setupView(){
        val name = DetailFragmentArgs.fromBundle(arguments as Bundle).name
        val description = DetailFragmentArgs.fromBundle(arguments as Bundle).description
        val imageUrl = DetailFragmentArgs.fromBundle(arguments as Bundle).imageUrl
        val createAt = DetailFragmentArgs.fromBundle(arguments as Bundle).createdAt
        val lat = DetailFragmentArgs.fromBundle(arguments as Bundle).lat
        val lon = DetailFragmentArgs.fromBundle(arguments as Bundle).lon

        binding?.apply{
            tvDetailName.text = name
            tvDetailDescription.text = description
            val location = "Lat : $lat | Lon : $lon"
            binding?.tvLocationDetail?.text = location
            Glide.with(requireActivity())
                .load(imageUrl)
                .into(ivDetailPhoto)
                tvCreatedat.text = Util.formattedDate(createAt.toString(), TimeZone.getDefault().id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}