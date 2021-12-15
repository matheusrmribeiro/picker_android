package com.arcondry.picker.android.features.base

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(
    private val savedStateHandle: SavedStateHandle
) :	ViewModel() {
    abstract val restoreScope: (SavedStateHandle) -> Unit
    abstract val saveScope: (SavedStateHandle) -> Unit

    init {
        restoreState()
    }

    private fun saveState() {
        saveScope.invoke(savedStateHandle)
    }

    private fun restoreState() {
        if (savedStateHandle.keys().isNotEmpty()) restoreScope.invoke(savedStateHandle)
    }

    override fun onCleared() {
        saveState()
        super.onCleared()
    }
}
