package com.arcondry.picker.android.features.livepicker.presentation

import androidx.lifecycle.ViewModel
import com.arcondry.picker.android.features.livepicker.data.entities.ColorEntity


class LivePickerViewModel : ViewModel() {

    val colorList = mutableListOf<ColorEntity>()

    fun addColor(color: Int) {
        val entity = ColorEntity(
            colorText = "#%X".format(color),
            color = color
        )
        colorList.add(entity)
    }

    fun removeColor(entity: ColorEntity) {
        colorList.remove(entity)
    }

}