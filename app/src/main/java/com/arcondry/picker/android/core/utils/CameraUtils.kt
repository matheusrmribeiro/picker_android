package com.arcondry.picker.android.core.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class CameraUtils(
    private var fragment: Fragment,
    private val cameraCallback: () -> Unit
) {
    // Handle permissions request result
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    fun registerActivityResult() {
        registerPermissionsResult()
    }

    private fun registerPermissionsResult() {
        with(fragment) {
            requestCameraPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) cameraCallback.invoke()
                }
        }
    }

    fun checkCameraPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.CAMERA
        )

        return if (cameraPermission == PackageManager.PERMISSION_GRANTED)
            true
        else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            false
        }
    }

}