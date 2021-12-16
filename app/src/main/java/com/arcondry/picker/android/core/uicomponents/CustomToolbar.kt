package com.arcondry.picker.android.core.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.arcondry.picker.android.databinding.UiCustomToolbarBinding

class CustomToolbar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var binding: UiCustomToolbarBinding =
        UiCustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    fun setTitle(title: String) {
        binding.customToolbar.title = title
    }

    fun setNavigateUp(callback: () -> Unit) {
        binding.customToolbar.setNavigationOnClickListener {
            callback.invoke()
        }
    }

    fun setMenu(menu: Int) {
        binding.customToolbar.inflateMenu(menu)
    }

}