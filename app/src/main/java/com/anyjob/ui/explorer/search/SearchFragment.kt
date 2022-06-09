package com.anyjob.ui.explorer.search

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.anyjob.R
import com.anyjob.databinding.FragmentSearchBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.radar.extensions.startRadar
import com.anyjob.ui.animations.radar.RadarParameters
import com.anyjob.ui.controls.GeolocationUnavailableBottomSheetDialog
import com.anyjob.ui.explorer.search.viewModels.SearchViewModel
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.anyjob.ui.extensions.showToast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
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

    private val _searchBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(_binding.searchBottomSheet.bottomSheetLayout)
    }

    private var _searchRadiiViews = ArrayList<Circle>()

    private fun getSearchRadius(chipId: Int): Float = when (chipId) {
        R.id.five_kilometers_chip -> 5000.0f
        R.id.ten_kilometers_chip -> 10000.0f
        else -> 2000.0f
    }

    private val _searchBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> drawSearchRadius(
                    _googleMap.cameraPosition.target,
                    getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)
                )

                BottomSheetBehavior.STATE_COLLAPSED -> removeLastSearchRadius()
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
            showLocationPermissionsRationaleDialog()
        }
    }

    private fun requestLocationPermissions() {
        _locationPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showLocationPermissionsRationaleDialog() {
        val context = requireContext()
        GeolocationUnavailableBottomSheetDialog(context).apply {
            show()
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
            return requestLocationPermissions()
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

    private fun drawSearchRadius(position: LatLng, radius: Float) {
        removeLastSearchRadius()

        val searchRadiusOptions = CircleOptions().apply {
            center(position)
            strokeColor(Color.TRANSPARENT)
            fillColor(
                Color.parseColor(
                    getString(R.color.light_purple)
                )
            )
        }

        _searchRadiiViews.add(
            _googleMap.addCircle(searchRadiusOptions).also {
                startRadar(
                    RadarParameters().apply {
                        mode = VisibilityMode.Show
                        animationLength = 2000
                        maxRadius = radius
                        onUpdate = { radiusFraction ->
                            it.radius = radiusFraction
                        }
                    }
                )
            }
        )
    }

    private fun removeLastSearchRadius() {
        _searchRadiiViews.lastOrNull()?.also {
            startRadar(
                RadarParameters().apply {
                    mode = VisibilityMode.Hide
                    animationLength = 500
                    maxRadius = it.radius.toFloat()
                    onUpdate = { radiusFraction ->
                        it.radius = radiusFraction

                        if (it.radius == 0.0) {
                            it.remove()
                            _searchRadiiViews.remove(it)
                        }
                    }
                }
            )
        }
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

                try {
                    val addresses = withContext(Dispatchers.Default) {
                        geocoder.getFromLocation(position.latitude, position.longitude, 1)
                    }

                    if (addresses.any()) {
                        val address = addresses[0]
                        _activityViewModel.updateCurrentAddress(address)
                    }
                }

                catch (exception: Exception) {
                    showToast(
                        getString(R.string.failed_to_determine_address)
                    )
                }

                finally {
                    if (_searchBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        drawSearchRadius(
                            position,
                            getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)
                        )
                    }
                }
            }
        }

        _googleMap.setOnCameraMoveStartedListener {
            removeLastSearchRadius()
        }

        moveCameraToUserLocation()
    }

    private fun onCurrentLocationButtonClick(button: View) {
        moveCameraToUserLocation()
    }

    private fun onUserChangeRadius(chipGroup: View, selectedChips: List<Int>) {
        drawSearchRadius(
            _googleMap.cameraPosition.target,
            getSearchRadius(
                selectedChips.first()
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        _mapView.getMapAsync(::onMapReady)
        _binding.currentLocationButton.setOnClickListener(::onCurrentLocationButtonClick)

        _searchBottomSheetBehavior.apply {
            addBottomSheetCallback(_searchBottomSheetCallback)
            isGestureInsetBottomIgnored=true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        _binding.searchBottomSheet.availableRadii.setOnCheckedStateChangeListener(::onUserChangeRadius)

        return _binding.root
    }
}