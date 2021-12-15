package com.arcondry.picker.android.features.home.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arcondry.picker.android.databinding.FragmentHomeBinding
import com.arcondry.picker.android.features.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun setupFragment() {
        binding.btnOpenLivePicker.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragment2ToHomeFragment()
            findNavController().navigate(direction)
        }
    }

}