package com.anyjob.ui.explorer.search.controls.bottomSheets

import android.content.Context
import android.content.DialogInterface
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import com.anyjob.databinding.AcceptJobBottomSheetBinding
import com.anyjob.domain.search.models.Order
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class AcceptJobBottomSheetDialog(
    private val explorerViewModel: ExplorerViewModel,
    private val order: Order,
    private val decodingAddress: String,
    private val onClientFound: (Order) -> Unit,
    private val onAcceptOrder: (Order) -> Unit,
    context: Context,
    theme: Int
) : BottomSheetDialog(context, theme) {
    private val _binding: AcceptJobBottomSheetBinding by lazy {
        AcceptJobBottomSheetBinding.inflate(layoutInflater)
    }

    private fun acceptButtonClick(button: View) {
        explorerViewModel.acceptJob(order)
        onAcceptOrder(order)
        dismiss()
    }

    private fun onCancel(dialogInterface: DialogInterface) {
        explorerViewModel.startClientSearching(onClientFound)
        dialogInterface.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        _binding.serviceCategory.text = order.service.category
        _binding.serviceTitle.text = order.service.title
        _binding.addressTextView.text = decodingAddress

        _binding.acceptButton.setOnClickListener(::acceptButtonClick)
        setOnCancelListener(::onCancel)
    }
}