package com.arcondry.picker.android.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.nio.ByteBuffer
import android.os.HandlerThread
import android.hardware.camera2.CaptureRequest

// OLD one

class CameraControllerOLD(
    private val context: Activity,
    private val surfaceView: SurfaceView
    ) : SurfaceHolder.Callback {

    private var camera = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val surfaceHolder: SurfaceHolder = this.surfaceView.holder

    init {
        surfaceHolder.addCallback(this)
    }

    @SuppressLint("MissingPermission")
    override fun surfaceCreated(holder: SurfaceHolder) {
        val currentCamera = camera.cameraIdList[0]

        camera.openCamera(currentCamera, object: CameraDevice.StateCallback() {
            override fun onDisconnected(p0: CameraDevice) { }
            override fun onError(p0: CameraDevice, p1: Int) { }

            override fun onOpened(cameraDevice: CameraDevice) {
                val previewSurface = surfaceView.holder.surface
                val reader: ImageReader = ImageReader.newInstance(
                    640,
                    480,
                    ImageFormat.JPEG, // ImageFormat.YUV_420_888,
                    1
                ).apply {
                    setOnImageAvailableListener(
                        onImageAvailableListener,
                        startBackgroundThread()
                    )
                }

                val captureCallback = object : CameraCaptureSession.StateCallback() {
                    override fun onConfigureFailed(session: CameraCaptureSession) {}

                    override fun onConfigured(session: CameraCaptureSession) {
                        val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            .apply {
                                addTarget(previewSurface)
                                addTarget(reader.surface)
                            }

                        session.setRepeatingRequest(
                            previewRequestBuilder.build(),
                            object: CameraCaptureSession.CaptureCallback() {},
                            startBackgroundThread()
                        )
                    }
                }

                cameraDevice.createCaptureSession(
                    mutableListOf(previewSurface, reader.surface),
                    captureCallback,
                    startBackgroundThread()
                )

            }
        }, startBackgroundThread())
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//        if (previewing)
//            pararVisualizacao()
//        try {
//            camera.setPreviewDisplay(surfaceHolder)
//            iniciarVisualizacao()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private val onImageAvailableListener = OnImageAvailableListener { imReader: ImageReader ->
        val image: Image = imReader.acquireLatestImage()
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        image.close()
    }

    private fun startBackgroundThread() : Handler {
//        return Handler(Looper.getMainLooper()) { true }
        val mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread.start()
        return Handler(mBackgroundThread.looper) { true }
    }
}