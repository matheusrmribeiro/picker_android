package com.arcondry.picker.android.core.utils

import android.graphics.Bitmap
import android.graphics.Matrix

class ImageUtils {
    companion object {
        fun Bitmap.rotate(degrees: Float): Bitmap {
            val matrix = Matrix().apply { postRotate(degrees) }
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }
    }
}