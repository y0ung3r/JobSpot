package com.jobspot.ui.explorer.search

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.jobspot.R
import com.jobspot.data.extensions.isGeolocationPermissionsDenied
import com.jobspot.databinding.FragmentSearchBinding
import com.jobspot.domain.profile.models.MapAddress
import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.OrderCreationParameters
import com.jobspot.domain.services.models.Service
import com.jobspot.ui.animations.VisibilityMode
import com.jobspot.ui.animations.extensions.fade
import com.jobspot.ui.animations.fade.FadeParameters
import com.jobspot.ui.animations.radar.RadarParameters
import com.jobspot.ui.animations.radar.RadarView
import com.jobspot.ui.explorer.ExplorerActivity
import com.jobspot.ui.explorer.search.controls.bottomSheets.GeolocationUnavailableBottomSheetDialog
import com.jobspot.ui.explorer.search.controls.bottomSheets.addresses.AddressesBottomSheetDialog
import com.jobspot.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.jobspot.ui.explorer.search.controls.bottomSheets.services.ServicesBottomSheetDialog
import com.jobspot.ui.explorer.search.viewModels.SearchViewModel
import com.jobspot.ui.explorer.viewModels.ExplorerViewModel
import com.jobspot.ui.extensions.getZoomLevel
import com.jobspot.ui.extensions.observeOnce
import com.jobspot.ui.extensions.showToast
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<ExplorerViewModel>()
    private val _viewModel by viewModel<SearchViewModel>()
    private lateinit var _binding: FragmentSearchBinding
    private lateinit var _yandexMap: Map
    private lateinit var _addressesBottomSheet: BottomSheetDialog
    private lateinit var _servicesBottomSheet: BottomSheetDialog

    private val _fragmentHost by lazy {
        requireActivity().findViewById<FragmentContainerView>(R.id.explorer_fragments_container)
    }

    private val _toolbar by lazy {
        val activity = requireActivity() as ExplorerActivity
        return@lazy activity.binding.toolbar
    }

    private val _navigationController by lazy {
        findNavController()
    }

    private val _mapView by lazy {
        requireActivity().findViewById<MapView>(R.id.map)
    }

    private val _searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
    }

    private val _radarView by lazy {
        requireActivity().findViewById<RadarView>(R.id.searchRadar)
    }

    private val _searchBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(_binding.searchBottomSheet.bottomSheetLayout)
    }

    private val _searchProgressBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(_binding.searchProgressBottomSheet.bottomSheetLayout)
    }

    private var _isRadiusChanged: Boolean = false

    private fun getSearchRadius(chipId: Int): Float = when (chipId) {
        R.id.one_kilometer_chip -> 1000.0f
        R.id.two_kilometers_chip -> 2000.0f
        R.id.three_kilometers_chip -> 3000.0f
        R.id.five_kilometers_chip -> 5000.0f
        else -> 500.0f
    }

    private val _searchBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (bottomSheet.isVisible) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        val searchRadius = getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)
                        removeLastSearchRadius()
                        drawSearchRadius(_yandexMap.cameraPosition.target, searchRadius)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> removeLastSearchRadius()

                    else -> { }
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            updateLogoAfterSliding(bottomSheet, slideOffset)
        }
    }

    private val _searchProgressBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // Ignore...
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            updateLogoAfterSliding(bottomSheet, slideOffset)
        }
    }

    private val _onCameraCallback = CameraListener { map, position, _, isFinished ->
        if (isFinished) {
            val searchListener = object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val geoObject = response.collection.children.firstNotNullOf { it.obj }
                    _activityViewModel.updateCurrentAddress(geoObject)
                }

                override fun onSearchError(error: Error) {
                    showToast(getString(R.string.failed_to_determine_address))
                }
            }

            _searchManager.submit(
                position.target,
                16,
                SearchOptions().apply {
                    searchTypes = SearchType.GEO.value
                    resultPageSize = 1
                },
                searchListener
            )

            if (!_isRadiusChanged)
                _searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            if (_isRadiusChanged)
                _isRadiusChanged = false
        }
        else {
            if (!_isRadiusChanged)
                _searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
        else {
            moveCameraToUserLocation()
        }
    }

    private fun requestLocationPermissions() {
        _locationPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showLocationPermissionsRationaleDialog() {
        val context = requireContext()

        GeolocationUnavailableBottomSheetDialog(context, R.style.Theme_JobSpot_BottomSheetDialog)
            .show()
    }

    private fun moveCamera(geoObject: GeoObject) {
        val radius = getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)
        val position = geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java)?.balloonPoint
            ?: return

        moveCamera(position, getZoomLevel(radius))
    }

    private fun moveCamera(location: Point, zoom: Float) {
        val cameraPosition = CameraPosition(location, zoom, 0.0f, 0.0f)
        val animation = Animation(Animation.Type.SMOOTH, 1.0f)
        _yandexMap.move(cameraPosition, animation, null)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun moveCameraToUserLocation() {
        if (requireContext().isGeolocationPermissionsDenied())
            return requestLocationPermissions()

        _locationProvider.lastLocation.addOnSuccessListener { location ->
            val radius = getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)

            if (location != null) {
                val point = Point(location.latitude, location.longitude)
                moveCamera(point, getZoomLevel(radius))
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun drawSearchRadius(position: Point, radius: Float, radarParameters: RadarParameters? = null) {
        val animationParameters = radarParameters ?: RadarParameters()

        _radarView.startNewPulse(_mapView, position, animationParameters.apply {
            maxRadius = radius
        })
    }

    private fun removeLastSearchRadius() {
        _radarView.stopLastPulse(RadarParameters().apply {
            animationLength = 500
        })
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun onMapReady(yandexMap: Map) {
        _yandexMap = yandexMap

        _binding.searchBottomSheet.bottomSheetLayout.isVisible = true
        _binding.currentLocationButton.isVisible = true

        val mapStyle = resources
            .openRawResource(R.raw.map_style)
            .bufferedReader()
            .use { it.readText() }

        _yandexMap.setMapStyle(mapStyle)

        _yandexMap.isRotateGesturesEnabled = false
        _yandexMap.isTiltGesturesEnabled = false

        _yandexMap.addCameraListener(_onCameraCallback)

        if (_yandexMap.isScrollGesturesEnabled)
            moveCameraToUserLocation()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun onCurrentLocationButtonClick(button: View) {
        moveCameraToUserLocation()
    }

    private fun onUserChangeRadius(chipGroup: View, selectedChip: Int) {
        _isRadiusChanged = true

        val radius = getSearchRadius(selectedChip)
        val location = _yandexMap.cameraPosition.target

        moveCamera(Point(location.latitude, location.longitude), getZoomLevel(radius))
        removeLastSearchRadius()
        drawSearchRadius(location, radius)
    }

    private fun onUserStartSearching(button: View) {
        _binding.currentLocationButton.fade(
            FadeParameters().apply {
                mode = VisibilityMode.Hide
                animationLength = 300
            }
        )

        _binding.searchBottomSheet.bottomSheetLayout.visibility = View.GONE
        _binding.searchProgressBottomSheet.bottomSheetLayout.visibility = View.VISIBLE

        _searchProgressBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        _binding.mapPin.isTouchEventsDisabled = true
        _yandexMap.isScrollGesturesEnabled = false
        _yandexMap.isZoomGesturesEnabled = false

        val chipId = _binding.searchBottomSheet.availableRadii.checkedChipId
        val chip = _binding.searchBottomSheet.availableRadii.findViewById<Chip>(chipId)
        val radius = getSearchRadius(chipId)

        val position = _yandexMap.cameraPosition.target
        val radarParameters = RadarParameters().apply {
            infinite = true
            animationLength = 3500
        }

        removeLastSearchRadius()
        drawSearchRadius(position, radius, radarParameters)
        moveCamera(_yandexMap.cameraPosition.target, getZoomLevel(radius))

        _binding.searchProgressBottomSheet.searchInProgressDescription.text = getString(R.string.search_progress_description, chip.text)

        _activityViewModel.getAuthorizedUser().observeOnce(this@SearchFragment) { authorizedUser ->
            authorizedUser?.also {
                _viewModel.service.observeOnce(this@SearchFragment) { service ->
                    _viewModel.startWorkerSearching(
                        OrderCreationParameters(
                            invokerId = it.id,
                            address = MapAddress(position.latitude, position.longitude),
                            radius.toDouble(),
                            service
                        ),
                        ::onWorkerFound
                    )
                    .observeOnce(this@SearchFragment) { order ->
                        _activityViewModel.setOrder(order)
                    }
                }
            }
        }
    }

    private fun onWorkerFound(worker: User) {
        _activityViewModel.setWorker(worker)
        _navigationController.navigate(R.id.path_to_order_overview_fragment_from_navigation_search)
    }

    private fun onUserCancelSearching(button: View) {
        _binding.currentLocationButton.fade(
            FadeParameters().apply {
                mode = VisibilityMode.Show
                animationLength = 300
            }
        )

        _searchProgressBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        _binding.searchProgressBottomSheet.bottomSheetLayout.visibility = View.GONE
        _binding.searchBottomSheet.bottomSheetLayout.visibility = View.VISIBLE

        _searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        _binding.mapPin.isTouchEventsDisabled = false
        _yandexMap.isScrollGesturesEnabled = true
        _yandexMap.isZoomGesturesEnabled = true

        removeLastSearchRadius()

        val position = _yandexMap.cameraPosition.target
        val radius = getSearchRadius(_binding.searchBottomSheet.availableRadii.checkedChipId)

        moveCamera(position, getZoomLevel(radius))

        _activityViewModel.order.observeOnce(this@SearchFragment) { order ->
            _viewModel.cancelWorkerSearching(order)
        }
    }

    private fun onAddressSelected(userAddress: UserAddress) {
        _addressesBottomSheet.dismiss()
        moveCamera(userAddress.geoObject)
    }

    private fun onAddressTitleClick(addressView: View) {
        _addressesBottomSheet = AddressesBottomSheetDialog(
            requireContext(),
            R.style.Theme_JobSpot_BottomSheetDialog,
            ::onAddressSelected
        )

        _addressesBottomSheet.show()
    }

    private fun onServiceSelected(service: Service) {
        _viewModel.setService(service)
        _binding.searchBottomSheet.selectServiceButton.text = service.title
        _binding.searchBottomSheet.startSearchingButton.visibility = View.VISIBLE
        _servicesBottomSheet.dismiss()
    }

    private fun openServicesBottomSheet(view: View) {
        _viewModel.getServicesList().observe(this@SearchFragment) { services ->
            _servicesBottomSheet = ServicesBottomSheetDialog(
                requireContext(),
                R.style.Theme_JobSpot_BottomSheetDialog,
                services,
                ::onServiceSelected
            )

            _servicesBottomSheet.show()
        }
    }

    private fun updateLogoUsingAbsoluteBottomSheetHeight(absoluteHeight: Int) {
        val newPadding = Padding(0, absoluteHeight)
        _mapView.mapWindow.map.logo.setPadding(newPadding)
    }

    private fun updateLogoAfterSliding(bottomSheet: View, slideOffset: Float) {
        if (!bottomSheet.isVisible)
            return

        val sheetBehaviour = when {
            _binding.searchBottomSheet.bottomSheetLayout == bottomSheet -> _searchBottomSheetBehavior
            _binding.searchProgressBottomSheet.bottomSheetLayout == bottomSheet -> _searchProgressBottomSheetBehavior
            else -> return
        }

        val peekHeight = sheetBehaviour.peekHeight.toFloat()
        val sheetHeight = (peekHeight + (bottomSheet.bottom - bottomSheet.top - peekHeight) * slideOffset).toInt()
        val newPadding = Padding(0, sheetHeight)
        _mapView.mapWindow.map.logo.setPadding(newPadding)
    }

    private fun onBottomSheetHeightChanged(bottomSheet: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        val newHeight = bottom - top
        val oldHeight = oldBottom - oldTop

        if (newHeight != oldHeight && bottomSheet.isVisible)
            updateLogoUsingAbsoluteBottomSheetHeight(newHeight)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        _binding.currentLocationButton.setOnClickListener(::onCurrentLocationButtonClick)
        _binding.currentLocationButton.isVisible = false
        _binding.searchBottomSheet.bottomSheetLayout.isVisible = false

        _fragmentHost.fitsSystemWindows = true

        _searchBottomSheetBehavior.apply {
            addBottomSheetCallback(_searchBottomSheetCallback)
            isGestureInsetBottomIgnored = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        _searchProgressBottomSheetBehavior.apply {
            addBottomSheetCallback(_searchProgressBottomSheetCallback)
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        
        _binding.searchBottomSheet.bottomSheetLayout.addOnLayoutChangeListener(::onBottomSheetHeightChanged)
        _binding.searchProgressBottomSheet.bottomSheetLayout.addOnLayoutChangeListener(::onBottomSheetHeightChanged)

        _binding.searchBottomSheet.selectServiceButton.setOnClickListener(::openServicesBottomSheet)
        _binding.searchBottomSheet.availableRadii.setOnCheckedChangeListener(::onUserChangeRadius)
        _binding.searchBottomSheet.startSearchingButton.setOnClickListener {
            val context = requireContext()

            val clarifyContainer = LinearLayout(context)
            clarifyContainer.gravity = Gravity.CENTER

            val textInput = TextInputEditText(context).apply {
                inputType = EditorInfo.TYPE_CLASS_NUMBER
                minWidth = 125
            }

            clarifyContainer.addView(textInput)

            val clarifyDialog = MaterialAlertDialogBuilder(context, R.style.Theme_JobSpot_AlertDialog)
                .setMessage(R.string.clarify_apartment_number)
                .setView(clarifyContainer)
                .setPositiveButton(R.string.confirm_entry_action) { dialog, id ->
                    onUserStartSearching(it)
                    dialog.cancel()
                }

            clarifyDialog.show()
        }

        _binding.searchProgressBottomSheet.cancelButton.setOnClickListener(::onUserCancelSearching)
        _toolbar.setOnClickListener(::onAddressTitleClick)

        return _binding.root
    }

    private val _mapLoadedListener = MapLoadedListener {
        onMapReady(_mapView.mapWindow.map)
    }

    override fun onStart() {
        super.onStart()
        _mapView.onStart()
        _mapView.mapWindow.map.setMapLoadedListener(_mapLoadedListener)
    }

    override fun onStop() {
        _mapView.onStop()
        super.onStop()
    }
}