package com.anyjob.ui.controls.bottomSheets

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.anyjob.databinding.AddressesBottomSheetBinding
import com.anyjob.ui.extensions.afterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddressesBottomSheetDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {
    private val _binding: AddressesBottomSheetBinding by lazy {
        AddressesBottomSheetBinding.inflate(layoutInflater)
    }

    private fun onAddressChanged(address: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        _binding.bottomSheetLayout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        _binding.addressField.afterTextChanged(::onAddressChanged)
    }
}