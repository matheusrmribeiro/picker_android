package com.arcondry.picker.android.features.livepicker.presentation.cell

import com.arcondry.picker.android.databinding.RecyclerviewCellColorBinding
import com.arcondry.picker.android.features.livepicker.data.entities.ColorEntity
import io.github.enicolas.genericadapter.adapter.BaseCell

class ColorCell(var viewBinding: RecyclerviewCellColorBinding) :
    BaseCell(viewBinding.root) {

    fun set(entity: ColorEntity, onRemoveCallback: () -> Unit) {
        viewBinding.viewColor.setBackgroundColor(entity.color)
        viewBinding.tvColorText.text = entity.colorText
        viewBinding.btnRemove.setOnClickListener {
            onRemoveCallback.invoke()
        }
    }

}
