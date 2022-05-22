package com.anyjob.ui.authorization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {
    private val _viewModel by viewModel<AuthorizationViewModel>()

    private val _binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()


    }
}