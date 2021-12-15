package com.arcondry.picker.android

import android.view.LayoutInflater
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.arcondry.picker.android.databinding.ActivityMainBinding
import com.arcondry.picker.android.features.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private val navigator by lazy { findNavController(R.id.nav_host) }

    override fun setupActivity() {
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
            ),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        binding.toolbar.setupWithNavController(navigator, appBarConfiguration)
    }
}