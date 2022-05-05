package com.anyjob.ui.explorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anyjob.R
import com.anyjob.databinding.ActivityExplorerBinding

class ExplorerActivity : AppCompatActivity() {
    private val binding: ActivityExplorerBinding by lazy {
        ActivityExplorerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        val navigationItems = setOf(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_notifications
        )

        val navigationController = findNavController(R.id.explorer_fragment_host)
        val applicationBarConfiguration = AppBarConfiguration(navigationItems)
        setupActionBarWithNavController(navigationController, applicationBarConfiguration)
        binding.navigationView.setupWithNavController(navigationController)
    }
}