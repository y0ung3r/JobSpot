package com.anyjob.ui.explorer.search.controls.bottomSheets.addresses

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.anyjob.R
import com.anyjob.databinding.AddressesBottomSheetBinding
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.adapters.AddressesAdapter
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.anyjob.ui.extensions.afterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
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

    private fun onAddressChanged(address: String) {
        val searchListener = object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                _addressesAdapter = AddressesAdapter(
                    response.collection.children.mapNotNull { it.obj },
                    onItemClick
                )

                _binding.addressesList.adapter = _addressesAdapter
                _addressesAdapter.notifyDataSetChanged()
            }

            override fun onSearchError(error: Error) {
                // Ignore...
            }
        }

        val southwest = Point(-90.0, -180.0)  // юго-западная точка
        val northeast = Point(90.0, 180.0)   // северо-восточная точка
        val boundingBox = BoundingBox(southwest, northeast)

        _searchManager.submit(
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

        _addressesAdapter = AddressesAdapter(
            ArrayList(),
            onItemClick
        )

        _binding.addressesList.adapter = _addressesAdapter
    }
}