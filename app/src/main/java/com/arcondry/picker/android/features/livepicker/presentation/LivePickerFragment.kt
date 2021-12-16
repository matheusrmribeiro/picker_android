package com.arcondry.picker.android.features.livepicker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.arcondry.picker.android.R
import com.arcondry.picker.android.core.utils.CameraController
import com.arcondry.picker.android.databinding.FragmentLivePickerBinding
import com.arcondry.picker.android.features.base.BaseFragment

class LivePickerFragment : BaseFragment<FragmentLivePickerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLivePickerBinding
        get() = FragmentLivePickerBinding::inflate

    private val cameraController: CameraController by lazy {
        CameraController(
            this,
            binding.previewView.surfaceProvider,
        )
    }

    override fun onDestroy() {
        cameraController.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }

    override fun onPause() {
        showToolbar()
        super.onPause()
    }

    override fun setupFragment() {
        cameraController.setupCameraPreview()
        setupObservers()
        setupToolbar()
    }

    private fun setupObservers() {
        cameraController.color.observe { color ->
            binding.imgColor.setColorFilter(color)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle("teste")
        binding.toolbar.setMenu(R.menu.live_picker_menu)
    }

}
