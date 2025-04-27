package com.anyjob.ui.explorer.search.controls.bottomSheets.services

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.anyjob.R
import com.anyjob.databinding.ServicesBottomSheetBinding
import com.anyjob.domain.services.models.Service
import com.anyjob.ui.explorer.search.controls.bottomSheets.services.adapters.ServicesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class ServicesBottomSheetDialog(
    context: Context,
    theme: Int,
    private val services: List<Service>,
    private val onItemClick: ((Service) -> Unit)? = null
) : BottomSheetDialog(context, theme) {
    private val _binding: ServicesBottomSheetBinding by lazy {
        ServicesBottomSheetBinding.inflate(layoutInflater)
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

        val adapter = ServicesAdapter(services, onItemClick)

        if (adapter.itemCount == 0) {
            _binding.servicesList.visibility = View.GONE
            _binding.emptyPrompt.visibility = View.VISIBLE
        }
        else {
            _binding.servicesList.visibility = View.VISIBLE
            _binding.emptyPrompt.visibility = View.GONE
        }

        _binding.servicesList.adapter = adapter
    }
}