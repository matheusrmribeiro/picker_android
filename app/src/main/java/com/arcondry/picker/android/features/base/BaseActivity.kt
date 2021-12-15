package com.arcondry.picker.android.features.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> :	AppCompatActivity() {
	abstract val bindingInflater: (LayoutInflater) -> VB
	private var _binding: VB? = null
	protected val binding get() = requireNotNull(_binding)
	protected var screenHasAlreadyBeenCreated: Boolean = false

	companion object {
		const val SCREEN_ALREADY_CREATED = "screenHasAlreadyBeenCreated"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		_binding = bindingInflater(layoutInflater)
		if (savedInstanceState?.containsKey(SCREEN_ALREADY_CREATED) == true)
			this.screenHasAlreadyBeenCreated = savedInstanceState.getBoolean(SCREEN_ALREADY_CREATED)
		setContentView(binding.root)
		setupActivity()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putBoolean(SCREEN_ALREADY_CREATED, true)
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}

	/**
	 * Hide or show system ui
	 */
	@Suppress("DEPRECATION")
	open fun setFullScreenMode(value: Boolean) {
		if (value) {
			val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
					or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
					or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
			window.decorView.systemUiVisibility = uiOptions
		}
		else {
			window.decorView.systemUiVisibility = 0
		}
	}

	abstract fun setupActivity()

}
