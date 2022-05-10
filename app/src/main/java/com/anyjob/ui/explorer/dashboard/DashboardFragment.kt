package com.anyjob.ui.explorer.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anyjob.databinding.FragmentDashboardBinding
import com.anyjob.ui.explorer.dashboard.viewModels.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {
    private val _viewModel by viewModel<DashboardViewModel>()
    private lateinit var _binding: FragmentDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        _viewModel.text.observe(viewLifecycleOwner) { content ->
            _binding.textDashboard.text = content
        }

        return _binding.root
    }
}