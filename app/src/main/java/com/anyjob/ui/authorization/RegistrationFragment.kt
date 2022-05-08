package com.anyjob.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anyjob.databinding.FragmentRegistrationBinding
import com.anyjob.ui.authorization.viewModels.RegistrationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationFragment : Fragment() {
    private val viewModel by viewModel<RegistrationViewModel>()
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        return binding.root
    }
}