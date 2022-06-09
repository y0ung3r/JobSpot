package com.anyjob.ui.controls.bottomSheets

import android.content.Context
import android.os.Bundle
import com.anyjob.databinding.AddressesBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddressesBottomSheetDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {
    private val _binding: AddressesBottomSheetBinding by lazy {
        AddressesBottomSheetBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
    }
}