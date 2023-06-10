package com.example.mystory.ui.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mystory.R
import com.example.mystory.data.Result
import com.example.mystory.data.model.StoryItem
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentMapsBinding
import com.example.mystory.factory.PrefViewModelFactory
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.MapsViewModel
import com.example.mystory.ui.viewmodel.PrefViewModel
import com.example.mystory.util.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
class MapsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding

    private lateinit var mMap: GoogleMap
    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(context as Context)
    }
    private val prefViewModel: PrefViewModel by viewModels {
        PrefViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
    }
    private lateinit var latLng: LatLng
    private val boundBuilder = LatLngBounds.Builder()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isScrollGesturesEnabledDuringRotateOrZoom = true
            isRotateGesturesEnabled = true
        }

        getMyLocation()
        setLocationMarker()
        setMapStyle()
    }

    private fun setLocationMarker() {
        prefViewModel.getUser().observe(viewLifecycleOwner) { result ->
            if (result.token.isNotEmpty()) {
                mapsViewModel.getLocation(result.token).observe(viewLifecycleOwner) { listStory ->
                    if (listStory != null) {
                        when (listStory) {
                            is Result.Loading,
                            is Result.Error,
                            is Result.ServerError,
                            is Result.Authorized -> {
                            }

                            is Result.Success -> {
                                listStory.data.forEach { item ->
                                    val lat = item.lat
                                    val lon = item.lon

                                    latLng = LatLng(lat, lon)
                                    val addressName = getAddressName(lat, lon)

                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title(item.name)
                                            .icon(
                                                vectorToBitmap(
                                                    R.drawable.baseline_location_on_24,
                                                    Color.parseColor(YELLOW_COLOR)
                                                )
                                            )
                                    )?.snippet = addressName

                                    boundBuilder.include(latLng)

                                    showCustomInfoWindow(item)
                                    mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            latLng,
                                            ZOOM_LEVEL
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCustomInfoWindow(item: StoryItem) {
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            private val view: View = View.inflate(context, R.layout.item_list_story, null)

            private fun windowInfo(marker: Marker, view: View) {
                val tvName = view.findViewById<TextView>(R.id.tvName)
                val tvCreatedAt = view.findViewById<TextView>(R.id.tvCreatedAt)
                val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
                tvName.text = marker.title
                tvCreatedAt.text = Util.formattedDate(item.createdAt, TimeZone.getDefault().id)
                tvDescription.text = StringBuilder(getString(R.string.text_location)).append(
                    marker.snippet ?: getString(R.string.not_available)
                )
            }

            override fun getInfoContents(marker: Marker): View {
                windowInfo(marker, view)
                return view
            }

            override fun getInfoWindow(marker: Marker): View {
                windowInfo(marker, view)
                return view
            }
        })
    }


    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun vectorToBitmap(
        @DrawableRes id: Int,
        @ColorInt color: Int
    ): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(activity?.resources as Resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(context as Context, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) getMyLocation()
    }


    companion object {
        const val TAG = "Maps_Fragment"
        const val YELLOW_COLOR = "#FFDC0A"
        const val ZOOM_LEVEL = 5f
    }
}