package com.anyjob.ui.explorer.search

import android.Manifest
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.anyjob.R
import com.anyjob.databinding.FragmentSearchBinding
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

    private val _bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(_binding.bottomSheet.bottomSheetLayout)
    }

    private var _searchRadiiViews = ArrayList<Circle>()

    private fun getSearchRadius(chipId: Int): Float = when (chipId) {
        R.id.five_kilometers_chip -> 5000.0f
        R.id.ten_kilometers_chip -> 10000.0f
        else -> 2000.0f
    }

    private val _bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> drawSearchRadius(
                    _googleMap.cameraPosition.target,
                    getSearchRadius(_binding.bottomSheet.availableRadii.checkedChipId)
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

            val headerText = getString(R.string.access_to_geolocation_alert_header)
            val descriptionText = getString(R.string.access_to_geolocation_alert_description)
            val positiveButtonText = getString(R.string.access_to_geolocation_alert_positive_button)

            builder.setTitle(headerText)
                   .setMessage(descriptionText)
                   .setPositiveButton(positiveButtonText) { _, _ ->
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

        // TODO: перекинуть анимацию в animation package
        _searchRadiiViews.add(
            _googleMap.addCircle(searchRadiusOptions).also {
                ValueAnimator().apply {
                    setFloatValues(0.0f, radius)
                    duration = 2000
                    interpolator = AccelerateDecelerateInterpolator()
                    setEvaluator(
                        FloatEvaluator()
                    )
                    addUpdateListener { valueAnimator ->
                        val ratio = valueAnimator.animatedFraction * radius
                        it.radius = ratio.toDouble()
                    }
                    start()
                }
            }
        )
    }

    private fun removeLastSearchRadius() {
        // TODO: перекинуть анимацию в animation package
        _searchRadiiViews.lastOrNull()?.also {
            val radius = it.radius.toFloat()
            ValueAnimator().apply {
                setFloatValues(0.0f, radius)
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
                setEvaluator(
                    FloatEvaluator()
                )
                addUpdateListener { valueAnimator ->
                    val ratio = valueAnimator.animatedFraction * radius
                    it.radius = ratio.toDouble()

                    if (it.radius == 0.0) {
                        it.remove()
                        _searchRadiiViews.remove(it)
                    }
                }
                reverse()
            }
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
                    if (_bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        drawSearchRadius(
                            position,
                            getSearchRadius(_binding.bottomSheet.availableRadii.checkedChipId)
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

        _bottomSheetBehavior.apply {
            addBottomSheetCallback(_bottomSheetCallback)
            isGestureInsetBottomIgnored=true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        _binding.bottomSheet.availableRadii.setOnCheckedStateChangeListener(::onUserChangeRadius)

        return _binding.root
    }
}