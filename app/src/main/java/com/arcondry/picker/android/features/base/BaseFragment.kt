package com.arcondry.picker.android.features.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.arcondry.picker.android.R
import com.arcondry.picker.android.core.uicomponents.CustomToolbar

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
	private var _binding: VB? = null
	protected val binding get() = requireNotNull(_binding)

	abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = bindingInflater.invoke(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupFragment()
		activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar)?.setNavigationOnClickListener {
			findNavController().navigateUp()
		}
	}

	fun showToolbar() {
		(activity as? AppCompatActivity)?.supportActionBar?.show()
	}

	fun hideToolbar() {
		(activity as? AppCompatActivity)?.supportActionBar?.hide()
	}

	fun setToolbarTitle(title: String) {
		(activity as? AppCompatActivity)?.supportActionBar?.title = title
	}

	fun setToolbarTitle(@StringRes title: Int) {
		(activity as? AppCompatActivity)?.supportActionBar?.setTitle(title)
	}

	abstract fun setupFragment()
	override fun onDestroyView() {
		super.onDestroyView()
		// Prevent memory leak with RecyclerView adapter
		_binding?.root?.allViews?.forEach {
			if (it is RecyclerView) {
				it.adapter = null
			}
		}
		_binding = null
	}

	protected fun <T> LiveData<T>.observe(observer: Observer<T>) = this.observe(viewLifecycleOwner, observer)
}
