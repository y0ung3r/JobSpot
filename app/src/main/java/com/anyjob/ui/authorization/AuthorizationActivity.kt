package com.anyjob.ui.authorization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {
    private val viewModel by viewModel<AuthorizationViewModel>()

    private val binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add(
            binding.authorizationFragmentsContainer.id,
            PhoneNumberEntryFragment()
        )
        .disallowAddToBackStack()
        .commit()
    }
}