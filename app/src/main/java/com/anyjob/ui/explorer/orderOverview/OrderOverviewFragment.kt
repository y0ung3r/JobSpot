package com.anyjob.ui.explorer.orderOverview

import android.app.AlertDialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.anyjob.R
import com.anyjob.databinding.FragmentOrderOverviewBinding
import com.anyjob.domain.profile.models.MapsAddress
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.models.Order
import com.anyjob.ui.explorer.ExplorerActivity
import com.anyjob.ui.explorer.orderOverview.viewModels.OrderOverviewViewModel
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.anyjob.ui.extensions.observeOnce
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class OrderOverviewFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<ExplorerViewModel>()
    private val _viewModel by viewModel<OrderOverviewViewModel>()

    private lateinit var _binding: FragmentOrderOverviewBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private val _fragmentHost by lazy {
        requireActivity().findViewById<FragmentContainerView>(R.id.explorer_fragments_container)
    }

    private val _toolbar by lazy {
        val activity = requireActivity() as ExplorerActivity
        return@lazy activity.binding.toolbar
    }

    private fun fillWorkerName(firstname: String) {
        _binding.orderOverviewWorkerName.text = firstname
    }

    private fun fillAddress(address: MapsAddress) {
        val geocoder = Geocoder(
            requireContext(),
            Locale.getDefault()
        )

        lifecycleScope.launch {
            val address = withContext(Dispatchers.Default) {
                geocoder.getFromLocation(address.latitude, address.longitude, 1)[0]
            }

            val street = address.thoroughfare
            val houseNumber = address.subThoroughfare

            _binding.addressTextView.text = "$street, $houseNumber"
        }
    }

    private fun onCall(worker: User) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:${worker.phoneNumber}")
        startActivity(callIntent)
    }

    private fun onCancel(order: Order) {
        val confirmationDialog = MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Theme_AnyJob_AlertDialog
            )
            .setTitle(R.string.cancel_order_alert_title)
            .setMessage(R.string.cancel_order_alert_description)
            .setPositiveButton("Подтвердить") { dialog, id ->
                _activityViewModel.cancelOrder(order)
            }
            .setNegativeButton("Отмена") { dialog, id ->
                dialog.cancel()
            }

        confirmationDialog.show()
    }

    private fun onOrderReady(worker: User, order: Order) {
        _activityViewModel.startOrderChecker(order) { isFinished, isCanceled ->
            onOrderChanged(worker, isFinished, isCanceled)
        }

        _binding.cancelButton.setOnClickListener {
            onCancel(order)
        }

        _binding.callButton.setOnClickListener {
            onCall(worker)
        }

        fillWorkerName(worker.firstname!!)
        fillAddress(order.address)

        _binding.serviceCategory.text = order.service.category
        _binding.serviceTitle.text = order.service.title
    }

    private fun showRatingDialog(worker: User) {
        val context = requireContext()

        val ratingBarContainer = LinearLayout(context)
        ratingBarContainer.gravity = Gravity.CENTER
        val ratingBar = RatingBar(context)
        ratingBar.numStars = 5
        ratingBar.stepSize = 0.5f
        ratingBar.rating = 0.0f
        ratingBarContainer.addView(ratingBar)

        val ratingDialog = MaterialAlertDialogBuilder(
            context,
            R.style.Theme_AnyJob_AlertDialog
        )
        .setTitle(R.string.order_finished_alert_title)
        .setMessage(R.string.rate_worker_description)
        .setView(ratingBarContainer)
        .setPositiveButton(R.string.confirm_entry_action) { dialog, id ->
            _activityViewModel.addRateToUser(worker, ratingBar.rating)
        }
        .setNegativeButton(R.string.cancel_entry_action) { dialog, id ->
            dialog.cancel()
        }

        ratingDialog.show()
    }

    private fun onOrderChanged(worker: User, isFinished: Boolean, isCanceled: Boolean) {
        if (isFinished) {
            showRatingDialog(worker)
        }

        if (isFinished || isCanceled) {
            _navigationController.navigate(R.id.path_to_navigation_search_from_order_overview_fragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOrderOverviewBinding.inflate(inflater, container, false)

        _fragmentHost.fitsSystemWindows = false
        _toolbar.title = null
        _toolbar.subtitle = null
        _toolbar.setOnClickListener(null)

        _activityViewModel.order.observeOnce(this@OrderOverviewFragment) { order ->
            _activityViewModel.worker.observeOnce(this@OrderOverviewFragment) { worker ->
                onOrderReady(worker, order)
            }
        }

        return _binding.root
    }
}