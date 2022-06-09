package com.anyjob.ui.controls.bottomSheets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.anyjob.databinding.GeolocationUnavailableBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class GeolocationUnavailableBottomSheetDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {
    private val _binding: GeolocationUnavailableBottomSheetBinding by lazy {
        GeolocationUnavailableBottomSheetBinding.inflate(layoutInflater)
    }

    private fun goToSettingsButtonClick(button: View) {
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts(
                "package",
                context.packageName,
                null
            )
        }

        startActivity(
            context,
            settingsIntent,
            Bundle.EMPTY
        )

        hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        _binding.goToSettingsButton.setOnClickListener(::goToSettingsButtonClick)
    }
}