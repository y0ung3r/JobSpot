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
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.anyjob.R
import com.anyjob.databinding.FragmentSearchBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.animations.radar.extensions.startRadar
import com.anyjob.ui.animations.radar.RadarParameters
import com.anyjob.ui.animations.slide.SlideFrom
import com.anyjob.ui.animations.slide.SlideParameters
import com.anyjob.ui.explorer.search.controls.bottomSheets.GeolocationUnavailableBottomSheetDialog
import com.anyjob.ui.explorer.search.viewModels.SearchViewModel
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.anyjob.ui.extensions.getZoomLevel
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

    private val _searchProgressBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(_binding.searchProgressBottomSheet.bottomSheetLayout)
    }

    private var _searchRadiiViews = ArrayList<Circle>()

    private fun getSearchRadius(chipId: Int): Float = when (chipId) {
        R.id.five_kilometers_chip -> 5000.0f
        R.id.ten_kilometers_chip -> 10000.0f
        else -> 2000.0f
    }

    private val _searchBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (bottomSheet.visibility == View.VISIBLE) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> drawSearchRadius(
                        _googleMap.cameraPosition.target,
                        getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)
                    )

                    BottomSheetBehavior.STATE_COLLAPSED -> removeLastSearchRadius()
                }
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

        GeolocationUnavailableBottomSheetDialog(
            context,
            R.style.Theme_AnyJob_BottomSheetDialog
        )
        .show()
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

    private fun moveCamera(location: LatLng, zoom: Float) {
        val coordinates = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, zoom)
        _googleMap.animateCamera(cameraUpdate)
    }

    private fun moveCameraToUserLocation() {
        if (isPermissionsDenied()) {
            return requestLocationPermissions()
        }

        if (!_googleMap.isMyLocationEnabled) {
            _googleMap.isMyLocationEnabled = true
        }

        _locationProvider.lastLocation.addOnSuccessListener { location ->
            moveCamera(
                LatLng(location.latitude, location.longitude),
                19.0f
            )
        }
    }

    private fun drawSearchRadius(position: LatLng, radius: Float, radarParameters: RadarParameters? = null): Circle {
        removeLastSearchRadius()

        val searchRadius = _googleMap.addCircle(
            CircleOptions().apply {
                center(position)
                strokeColor(Color.TRANSPARENT)
                fillColor(
                    Color.parseColor(
                        getString(R.color.light_purple)
                    )
                )
            }
        )

        startRadar(
            RadarParameters().apply {
                mode = VisibilityMode.Show

                if (radarParameters != null) {
                    infinity = radarParameters.infinity
                    animationLength = radarParameters.animationLength
                }
                else {
                    infinity = false
                    animationLength = 2000
                }

                maxRadius = radius

                val fillColor = Color.alpha(searchRadius.fillColor)
                onUpdate = { radiusFraction, invertedRadiusFraction ->
                    searchRadius.radius = radiusFraction

                    if (infinity) {
                        val alpha = (invertedRadiusFraction / radius * fillColor).toInt()
                        searchRadius.fillColor = ColorUtils.setAlphaComponent(searchRadius.fillColor, alpha)
                    }
                }
            }
        )

        _searchRadiiViews.add(searchRadius)

        return searchRadius
    }

    private fun removeLastSearchRadius() {
        _searchRadiiViews.lastOrNull()?.also {
            startRadar(
                RadarParameters().apply {
                    mode = VisibilityMode.Hide
                    animationLength = 500
                    maxRadius = it.radius.toFloat()
                    onUpdate = { radiusFraction, _ ->
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
                    _searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        _googleMap.setOnCameraMoveStartedListener {
            _searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        moveCameraToUserLocation()
    }

    private fun onCurrentLocationButtonClick(button: View) {
        moveCameraToUserLocation()
    }

    private fun onUserChangeRadius(chipGroup: View, selectedChip: Int) {
        drawSearchRadius(
            _googleMap.cameraPosition.target,
            getSearchRadius(selectedChip)
        )
    }

    private fun onUserStartSearching(button: View) {
        _binding.currentLocationButton.slide(
            SlideParameters().apply {
                from = SlideFrom.Left
                mode = VisibilityMode.Hide
                animationLength = 700
            }
        )
        _binding.searchBottomSheet.bottomSheetLayout.visibility = View.GONE
        _binding.searchProgressBottomSheet.bottomSheetLayout.visibility = View.VISIBLE

        _googleMap.uiSettings.isScrollGesturesEnabled = false
        _googleMap.uiSettings.isZoomGesturesEnabled = false

        val radius = getSearchRadius(
            _binding.searchBottomSheet.availableRadii.checkedChipId
        )

        val position = _googleMap.cameraPosition.target
        val radarParameters = RadarParameters().apply {
            infinity = true
            animationLength = 3500
        }

        drawSearchRadius(position, radius, radarParameters)

        moveCamera(
            position,
            getZoomLevel(
                radius.toDouble()
            )
        )
    }

    private fun onUserCancelSearching(button: View) {
        _binding.currentLocationButton.slide(
            SlideParameters().apply {
                from = SlideFrom.Left
                mode = VisibilityMode.Show
                animationLength = 700
            }
        )

        _binding.searchProgressBottomSheet.bottomSheetLayout.visibility = View.GONE
        _binding.searchBottomSheet.bottomSheetLayout.visibility = View.VISIBLE

        _searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        _googleMap.uiSettings.isScrollGesturesEnabled = true
        _googleMap.uiSettings.isZoomGesturesEnabled = true

        removeLastSearchRadius()

        val position = _googleMap.cameraPosition.target
        moveCamera(
            position,
            19.0f
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        _mapView.getMapAsync(::onMapReady)
        _binding.currentLocationButton.setOnClickListener(::onCurrentLocationButtonClick)

        _searchBottomSheetBehavior.apply {
            addBottomSheetCallback(_searchBottomSheetCallback)
            isGestureInsetBottomIgnored = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        _searchProgressBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        _binding.searchBottomSheet.availableRadii.setOnCheckedChangeListener(::onUserChangeRadius)
        _binding.searchBottomSheet.startSearchingButton.setOnClickListener(::onUserStartSearching)
        _binding.searchProgressBottomSheet.cancelButton.setOnClickListener(::onUserCancelSearching)

        return _binding.root
    }
}