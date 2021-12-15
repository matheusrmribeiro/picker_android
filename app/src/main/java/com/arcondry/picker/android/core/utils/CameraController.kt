package com.arcondry.picker.android.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arcondry.picker.android.core.utils.ImageUtils.Companion.rotate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraController(
    private val context: Fragment,
    private val surfaceProvider: Preview.SurfaceProvider,
) {

    companion object {
        private const val TAG = "CameraXBasic"
    }

    private val _color = MutableLiveData(0)
    val color: LiveData<Int>
        get() = _color

    private val _bitmap = MutableLiveData<Bitmap?>(null)
    val bitmap: LiveData<Bitmap?>
        get() = _bitmap

    private val cameraUtils = CameraUtils(context, ::createCameraController)

    private lateinit var cameraExecutor: ExecutorService

    fun onDestroy() {
        cameraExecutor.shutdown()
    }

    fun setupCameraPreview() {
        cameraUtils.registerActivityResult()
        if (cameraUtils.checkCameraPermission())
            createCameraController()
    }

    private fun createCameraController() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context.requireActivity())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(context.requireContext()) { bitmap ->
                        Log.d(TAG, "Image analyse")

                        bitmap?.let {
                            val centerX = bitmap.width / 2
                            val centerY = (bitmap.height * 0.35).toInt()
                            val color = getPixelColor(bitmap, centerX, centerY)
                            Log.d("pixel color", color.toString())
                            _color.postValue(color)
                            _bitmap.postValue(bitmap)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context.requireActivity()))
    }

    private class ImageAnalyzer(
        private val context: Context,
        private val listener: (Bitmap?) -> Unit
    ) : ImageAnalysis.Analyzer {

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            imageProxy.image?.let {
                val bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                YuvToRgbConverter(context = context).yuvToRgb(it, bitmap)
                // Fix the image orientation to portrait
                listener(bitmap.rotate(90f))
            }
            imageProxy.close()
        }
    }

    private fun getPixelColor(bitmap: Bitmap, x: Int, y: Int) : Int {
        val pixel: Int = bitmap.getPixel(x, y)
        val redValue: Int = Color.red(pixel)
        val greenValue: Int = Color.green(pixel)
        val blueValue: Int = Color.blue(pixel)
        return Color.rgb(redValue, greenValue, blueValue)
    }

}