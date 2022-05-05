package com.anyjob.ui.explorer.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anyjob.databinding.FragmentDashboardBinding
import com.anyjob.ui.explorer.dashboard.viewModels.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {
    private val viewModel by viewModel<DashboardViewModel>()
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        viewModel.text.observe(viewLifecycleOwner, { content ->
            binding.textDashboard.text = content
        })

        return binding.root
    }
}