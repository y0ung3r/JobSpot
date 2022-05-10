package com.anyjob.ui.authorization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class AuthorizationActivity : AppCompatActivity() {
    private val _binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    private fun navigateToPhoneNumberEntryFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add<PhoneNumberEntryFragment>(
            _binding.authorizationFragmentsContainer.id
        )
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        supportActionBar?.hide()

        navigateToPhoneNumberEntryFragment()
    }
}