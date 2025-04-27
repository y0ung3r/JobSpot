package com.anyjob.ui.explorer.jobOverview

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.anyjob.R
import com.anyjob.databinding.FragmentJobOverviewBinding
import com.anyjob.domain.profile.models.MapAddress
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.models.Order
import com.anyjob.ui.explorer.ExplorerActivity
import com.anyjob.ui.explorer.orderOverview.viewModels.OrderOverviewViewModel
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.anyjob.ui.extensions.observeOnce
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobOverviewFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<ExplorerViewModel>()
    private val _viewModel by viewModel<OrderOverviewViewModel>()

    private lateinit var _binding: FragmentJobOverviewBinding
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

    private fun fillAddress(address: MapAddress) {
        val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)

        val searchListener = object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val geoObject = response.collection.children.firstNotNullOf { it.obj }
                _binding.addressTextView.text = geoObject.name
            }

            override fun onSearchError(error: Error) {
                // showToast(getString(R.string.failed_to_determine_address))
            }
        }

        searchManager.submit(
            Point(address.latitude, address.longitude),
            16,
            SearchOptions().apply {
                searchTypes = SearchType.GEO.value
                resultPageSize = 1
            },
            searchListener
        )
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
            .setPositiveButton(R.string.confirm_entry_action) { dialog, id ->
                _activityViewModel.cancelOrder(order)
            }
            .setNegativeButton(R.string.cancel_entry_action) { dialog, id ->
                dialog.cancel()
            }

        confirmationDialog.show()
    }

    private fun onOrderReady(client: User, order: Order) {
        _binding.callButton.setOnClickListener {
            onCall(client)
        }

        _activityViewModel.startOrderChecker(order) { isFinished, isCanceled ->
            onOrderChanged(client, isFinished, isCanceled)
        }

        _binding.cancelButton.setOnClickListener {
            onCancel(order)
        }

        _binding.finishButton.setOnClickListener {
            _activityViewModel.finishOrder(order)
        }

        fillAddress(order.address)

        _binding.serviceCategory.text = order.service.category
        _binding.serviceTitle.text = order.service.title
    }

    private fun showRatingDialog(client: User) {
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
        .setMessage(R.string.rate_your_client_description)
        .setView(ratingBarContainer)
        .setPositiveButton(R.string.confirm_entry_action) { dialog, id ->
            _activityViewModel.addRateToUser(client, ratingBar.rating)
        }
        .setNegativeButton(R.string.cancel_entry_action) { dialog, id ->
            dialog.cancel()
        }

        ratingDialog.show()
    }

    private fun onOrderChanged(client: User, isFinished: Boolean, isCanceled: Boolean) {
        if (isFinished) {
            showRatingDialog(client)
        }

        if (isFinished || isCanceled) {
            _navigationController.navigate(R.id.path_to_navigation_search_from_job_overview_fragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentJobOverviewBinding.inflate(inflater, container, false)

        _fragmentHost.fitsSystemWindows = false
        _toolbar.title = null
        _toolbar.subtitle = null
        _toolbar.setOnClickListener(null)

        _activityViewModel.order.observeOnce(this@JobOverviewFragment) { order ->
            _activityViewModel.client.observeOnce(this@JobOverviewFragment) { client ->
                onOrderReady(client, order)
            }
        }

        return _binding.root
    }
}