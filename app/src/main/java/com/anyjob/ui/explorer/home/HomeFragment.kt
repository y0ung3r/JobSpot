package com.anyjob.ui.explorer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anyjob.databinding.FragmentHomeBinding
import com.anyjob.ui.explorer.home.viewModels.HomeViewModel
import com.google.android.gms.maps.GoogleMap
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private val _viewModel by viewModel<HomeViewModel>()
    private lateinit var _binding: FragmentHomeBinding

    private fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        //googleMap.isMyLocationEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _binding.map.getMapAsync(::onMapReady)
        return _binding.root
    }
}