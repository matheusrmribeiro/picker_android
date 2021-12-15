package com.arcondry.picker.android.core.utils

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.arcondry.picker.android.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * The class must be initialized before fragment OnCreate
 * to register the activity results correctly
 */
class PhotoUtils(
    private var fragment: Fragment,
    private val cameraCallback: () -> Unit
) {
    private var photoImageFile: File? = null

    // Handle permissions request result
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestGalleryPermissionsLauncher: ActivityResultLauncher<Array<String>>

    // Handle photo taken result
    private lateinit var getCameraImage: ActivityResultLauncher<Uri>

    // Handle photo taken result
    private lateinit var getGalleryImage: ActivityResultLauncher<String>

    fun registerActivityResult(
        onImagePicked: ((file: File, bitmap: Bitmap, fileName: String) -> Unit)? = null,
        onImageError: (() -> Unit)? = null
    ) {
        registerPermissionsResult()
        registerPhotoResult(onImagePicked, onImageError)
    }

    private fun registerPermissionsResult() {
        with(fragment) {
            requestCameraPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) choosePhoto()
                }

            requestGalleryPermissionsLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                    if (results.all { it.value }) takePhoto()
                }
        }
    }

    private fun registerPhotoResult(
        onImagePicked: ((file: File, bitmap: Bitmap, fileName: String) -> Unit)? = null,
        onImageError: (() -> Unit)? = null
    ) {
        with(fragment) {
            getCameraImage =
                registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                    if (success) photoImageFile?.let {
                        onImagePicked?.invoke(it, BitmapFactory.decodeFile(it.path), it.name)
                    } else onImageError?.invoke()
                }

            getGalleryImage =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    uri?.let {
                        val imageStream = requireContext().contentResolver.openInputStream(uri)
                        createImageFile()?.let { file ->
                            file.outputStream().use { imageStream?.copyTo(it) }
                            onImagePicked?.invoke(
                                file,
                                BitmapFactory.decodeFile(file.path),
                                file.name
                            )
                        }
                    }
                }
        }
    }

    fun checkCameraPermission(): Boolean {
        val cameraPermission = checkSelfPermission(fragment.requireContext(), CAMERA)

        return if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(CAMERA)
            false
        } else true
    }

    fun checkGalleryPermissions(): Boolean {
        val writeExternal = checkSelfPermission(fragment.requireContext(), WRITE_EXTERNAL_STORAGE)
        val readExternal = checkSelfPermission(fragment.requireContext(), READ_EXTERNAL_STORAGE)
        val listPermissionsNeeded = ArrayList<String>()

        if (writeExternal != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(WRITE_EXTERNAL_STORAGE)
        if (readExternal != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(READ_EXTERNAL_STORAGE)

        return if (listPermissionsNeeded.isNotEmpty()) {
            requestGalleryPermissionsLauncher.launch(listPermissionsNeeded.toTypedArray())
            false
        } else true
    }

    // Create an image file name
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.ROOT).format(Date())
        val fileName = "JPEG_" + timeStamp + "_"
        fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?.let { storageDir ->
                return File.createTempFile(fileName, ".jpg", storageDir)
            }
        return null
    }

    fun takePhoto() {
        if (checkCameraPermission()) {
            photoImageFile = createImageFile()
            photoImageFile?.let {
                val uri = FileProvider.getUriForFile(
                    fragment.requireContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
                )
                getCameraImage.launch(uri)
            }

        }
    }

    fun choosePhoto() {

        if (checkGalleryPermissions()) {
            getGalleryImage.launch("image/*")
        }

    }

}