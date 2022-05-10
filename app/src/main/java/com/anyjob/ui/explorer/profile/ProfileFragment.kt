package com.anyjob.ui.explorer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anyjob.databinding.FragmentProfileBinding
import com.anyjob.ui.explorer.profile.viewModels.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val _viewModel by viewModel<ProfileViewModel>()
    private lateinit var _binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        _viewModel.text.observe(viewLifecycleOwner) { content ->
            _binding.textNotifications.text = content
        }

        return _binding.root
    }
}