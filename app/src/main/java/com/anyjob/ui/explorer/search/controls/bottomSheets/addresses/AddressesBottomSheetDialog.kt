package com.anyjob.ui.explorer.search.controls.bottomSheets.addresses

import android.content.Context
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.anyjob.R
import com.anyjob.databinding.AddressesBottomSheetBinding
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.adapters.AddressesAdapter
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.anyjob.ui.extensions.afterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class AddressesBottomSheetDialog(
    context: Context,
    theme: Int,
    private val onItemClick: ((UserAddress) -> Unit)? = null
) : BottomSheetDialog(context, theme) {
    private val _binding: AddressesBottomSheetBinding by lazy {
        AddressesBottomSheetBinding.inflate(layoutInflater)
    }

    private lateinit var _addressesAdapter: AddressesAdapter

    private fun onAddressChanged(address: String) {
        GlobalScope.launch {
            try {
                val maxResults = 10
                val geocoder = Geocoder(
                    context,
                    Locale.getDefault()
                )

                val addresses = withContext(Dispatchers.Default) {
                    geocoder.getFromLocationName(address, maxResults)
                }

                _addressesAdapter = AddressesAdapter(
                    addresses,
                    onItemClick
                )

                _binding.addressesList.adapter = _addressesAdapter
                _addressesAdapter.notifyDataSetChanged()
            }

            catch (exception: Exception) {
                // Ignore...
            }
        }
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
            ArrayList<Address>(),
            onItemClick
        )

        _binding.addressesList.adapter = _addressesAdapter
    }
}