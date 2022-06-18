package com.anyjob.ui.explorer.orderOverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anyjob.databinding.FragmentOrderOverviewBinding
import com.anyjob.ui.explorer.ExplorerActivity
import com.anyjob.ui.explorer.orderOverview.viewModels.OrderOverviewViewModel
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderOverviewFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<ExplorerViewModel>()
    private val _viewModel by viewModel<OrderOverviewViewModel>()

    private lateinit var _binding: FragmentOrderOverviewBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private val _toolbar by lazy {
        val activity = requireActivity() as ExplorerActivity
        return@lazy activity.binding.toolbar
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOrderOverviewBinding.inflate(inflater, container, false)

        _toolbar.title = null
        _toolbar.subtitle = null

        return _binding.root
    }
}