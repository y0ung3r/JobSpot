package com.anyjob.ui.explorer.search

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.anyjob.R
import com.anyjob.databinding.FragmentSearchBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.fade
import com.anyjob.ui.animations.fade.FadeParameters
import com.anyjob.ui.explorer.search.viewModels.SearchViewModel
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<ExplorerViewModel>()
    private val _viewModel by viewModel<SearchViewModel>()
    private lateinit var _binding: FragmentSearchBinding
    private lateinit var _googleMap: GoogleMap

    private val _mapView by lazy {
        childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    private val _bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(_binding.bottomSheetLayout)
    }

    private val _bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> drawSearchRadius(
                    _googleMap.cameraPosition.target
                )

                else -> _googleMap.clear()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Ignore...
        }
    }

    private val _locationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
    }

    private val _locationPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            showRationaleDialogIfNeeded()
        }
    }

    private fun requestLocationPermissions() {
        _locationPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showRationaleDialogIfNeeded() {
        val context = requireContext()
        val activity = requireActivity()

        val shouldShowFinePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val shouldShowCoarsePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (shouldShowFinePermissionRationale || shouldShowCoarsePermissionRationale) {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Доступ к геолокации")
                   .setMessage("Разрешите доступ к геопозиции, иначе приложение не сможет определить где Вы находитесь")
                   .setPositiveButton("Хорошо") { _, _ ->
                       requestLocationPermissions()
                   }

            val rationaleDialog = builder.create()
            return rationaleDialog.show()
        }
    }

    private fun isPermissionsDenied(): Boolean {
        val context = requireContext()

        val finePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return !finePermissionGranted || !coarsePermissionGranted
    }

    private fun moveCameraToUserLocation() {
        if (isPermissionsDenied()) {
            return _locationPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!_googleMap.isMyLocationEnabled) {
            _googleMap.isMyLocationEnabled = true
        }

        _locationProvider.lastLocation.addOnSuccessListener { location ->
            val coordinates = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 19f)
            _googleMap.animateCamera(cameraUpdate)
        }
    }

    private fun drawSearchRadius(position: LatLng) {
        _googleMap.clear()

        val searchRadiusOptions = CircleOptions().apply {
            center(position)
            radius(20.0)
            strokeColor(android.R.color.transparent)
            fillColor(R.color.half_transparent_purple)
            strokeWidth(10.0f)
        }

        _googleMap.addCircle(searchRadiusOptions)
    }

    private fun onMapReady(googleMap: GoogleMap) {
        _googleMap = googleMap

        _googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style
            )
        )

        val geocoder = Geocoder(
            requireContext(),
            Locale.getDefault()
        )

        _googleMap.uiSettings.isMyLocationButtonEnabled = true
        _googleMap.uiSettings.isRotateGesturesEnabled = false
        _googleMap.uiSettings.isTiltGesturesEnabled = false
        _googleMap.uiSettings.isMyLocationButtonEnabled = false

        _googleMap.setOnCameraIdleListener {
            lifecycleScope.launch {
                val position = _googleMap.cameraPosition.target
                val addresses = withContext(Dispatchers.Default) {
                    geocoder.getFromLocation(position.latitude, position.longitude, 1)
                }

                if (addresses.any()) {
                    val address = addresses[0]
                    _activityViewModel.updateCurrentAddress(address)
                }

                if (_bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    drawSearchRadius(position)
                }
            }
        }

        moveCameraToUserLocation()
    }

    private fun onCurrentLocationButtonClick(button: View) {
        moveCameraToUserLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        _mapView.getMapAsync(::onMapReady)
        _binding.currentLocationButton.setOnClickListener(::onCurrentLocationButtonClick)

        _bottomSheetBehavior.apply {
            addBottomSheetCallback(_bottomSheetCallback)
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        return _binding.root
    }
}