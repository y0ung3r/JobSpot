package com.jobspot.ui.explorer.search.controls.bottomSheets.addresses

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.jobspot.R
import com.jobspot.databinding.AddressesBottomSheetBinding
import com.jobspot.ui.explorer.search.controls.bottomSheets.addresses.adapters.AddressesAdapter
import com.jobspot.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.jobspot.ui.extensions.afterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error

class AddressesBottomSheetDialog(
    context: Context,
    theme: Int,
    private val onItemClick: ((UserAddress) -> Unit)? = null
) : BottomSheetDialog(context, theme) {
    private val _binding: AddressesBottomSheetBinding by lazy {
        AddressesBottomSheetBinding.inflate(layoutInflater)
    }

    private lateinit var _addressesAdapter: AddressesAdapter
    private val _searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
    }

    private var _lastSession: Session? = null

    private fun onAddressChanged(address: String) {
        _lastSession?.cancel()

        val searchListener = object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val geoObjects = response.collection.children.mapNotNull { it.obj }

                if (geoObjects.isEmpty()) {
                    _binding.addressesActionPrompt.text = context.getString(R.string.empty_addresses_prompt)
                    _binding.addressesActionPromptContainer.visibility = View.VISIBLE
                    _binding.addressesList.visibility = View.GONE
                }
                else {
                    _binding.addressesActionPromptContainer.visibility = View.GONE
                    _binding.addressesList.visibility = View.VISIBLE
                }

                _addressesAdapter = AddressesAdapter(geoObjects, onItemClick)

                _binding.addressesList.adapter = _addressesAdapter
                _addressesAdapter.notifyDataSetChanged()
            }

            override fun onSearchError(error: Error) {
                _binding.addressesActionPrompt.text = context.getString(R.string.address_search_failed_prompt)
                _binding.addressesActionPromptContainer.visibility = View.VISIBLE
                _binding.addressesList.visibility = View.GONE
            }
        }

        val southwest = Point(-90.0, -180.0)
        val northeast = Point(90.0, 180.0)
        val boundingBox = BoundingBox(southwest, northeast)

        _lastSession = _searchManager.submit(
            address,
            Geometry.fromBoundingBox(boundingBox),
            SearchOptions().apply {
                resultPageSize = 24
            },
            searchListener
        )
    }

    private fun setupFullHeight() {
        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(_binding.root)
        setupFullHeight()

        _binding.addressField.afterTextChanged(::onAddressChanged)

        _addressesAdapter = AddressesAdapter(ArrayList(), onItemClick)

        _binding.addressesList.adapter = _addressesAdapter

        _binding.addressesActionPrompt.text = context.getString(R.string.start_search_prompt)
        _binding.addressesActionPromptContainer.visibility = View.VISIBLE
        _binding.addressesList.visibility = View.GONE
    }
}