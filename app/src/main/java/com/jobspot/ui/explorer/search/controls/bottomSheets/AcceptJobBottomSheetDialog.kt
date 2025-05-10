package com.jobspot.ui.explorer.search.controls.bottomSheets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.jobspot.databinding.AcceptJobBottomSheetBinding
import com.jobspot.domain.search.models.Order
import com.jobspot.ui.explorer.viewModels.ExplorerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jobspot.domain.profile.models.User

class AcceptJobBottomSheetDialog(
    private val explorerViewModel: ExplorerViewModel,
    private val client: User,
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
        _binding.clientRating.text = "%.1f".format(client.averageRate)

        _binding.acceptButton.setOnClickListener(::acceptButtonClick)
        setOnCancelListener(::onCancel)
    }
}