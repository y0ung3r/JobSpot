package com.anyjob.ui.authorization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {
    private val binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    private fun navigateToPhoneNumberEntryFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add<PhoneNumberEntryFragment>(
            binding.authorizationFragmentsContainer.id
        )
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        navigateToPhoneNumberEntryFragment()
    }
}