package com.arcondry.picker.android.features.livepicker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arcondry.picker.android.R
import com.arcondry.picker.android.core.utils.CameraController
import com.arcondry.picker.android.databinding.FragmentLivePickerBinding
import com.arcondry.picker.android.databinding.RecyclerviewCellColorBinding
import com.arcondry.picker.android.features.base.BaseFragment
import com.arcondry.picker.android.features.livepicker.data.entities.ColorEntity
import com.arcondry.picker.android.features.livepicker.presentation.cell.ColorCell
import io.github.enicolas.genericadapter.AdapterHolderType
import io.github.enicolas.genericadapter.adapter.GenericRecyclerAdapter
import io.github.enicolas.genericadapter.adapter.GenericRecylerAdapterDelegate
import io.github.enicolas.genericadapter.diffable.Snapshot

class LivePickerFragment : BaseFragment<FragmentLivePickerBinding>() {

    private val viewModel by viewModels<LivePickerViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLivePickerBinding
        get() = FragmentLivePickerBinding::inflate

    private val cameraController: CameraController by lazy {
        CameraController(
            this,
            binding.previewView.surfaceProvider,
        )
    }

    private val mAdapter =
        GenericRecyclerAdapter(Snapshot(object : DiffUtil.ItemCallback<ColorEntity>() {
            override fun areItemsTheSame(oldItem: ColorEntity, newItem: ColorEntity): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: ColorEntity, newItem: ColorEntity): Boolean {
                return oldItem.colorText == newItem.colorText
            }
        }))

    override fun onDestroy() {
        cameraController.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }

    override fun onPause() {
        showToolbar()
        super.onPause()
    }

    override fun setupFragment() {
        cameraController.setupCameraPreview()
        setupObservers()
        setupToolbar()
        setupRecycler()
    }

    private fun setupObservers() {
        cameraController.color.observe { color ->
            binding.imgColor.setColorFilter(color)
        }
        binding.btnTake.setOnClickListener {
            cameraController.color.value?.let {
                viewModel.addColor(it)
                notifyList()
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle("")
        binding.toolbar.setMenu(R.menu.live_picker_menu)
    }

    private fun setupRecycler() {
        mAdapter.delegate = recyclerViewDelegate
        binding.rcvColors.apply {
            adapter = mAdapter
            itemAnimator = null
        }
    }

    private fun notifyList() {
        mAdapter.snapshot?.snapshotList = mutableListOf<ColorEntity>().apply {
            addAll(viewModel.colorList)
        }
    }

    private val recyclerViewDelegate = object : GenericRecylerAdapterDelegate {
        override fun numberOfRows(adapter: GenericRecyclerAdapter): Int {
            return adapter.snapshot?.snapshotList?.size ?: 0
        }

        override fun cellForPosition(
            adapter: GenericRecyclerAdapter,
            cell: RecyclerView.ViewHolder,
            position: Int
        ) {
            (adapter.snapshot?.snapshotList?.get(position) as? ColorEntity)?.let { item ->
                (cell as? ColorCell)?.set(entity = item) {
                    viewModel.removeColor(item)
                    notifyList()
                }
            }
        }

        override fun registerCellAtPosition(
            adapter: GenericRecyclerAdapter,
            position: Int
        ): AdapterHolderType {
            return AdapterHolderType(
                RecyclerviewCellColorBinding::class.java,
                ColorCell::class.java,
                0
            )
        }

        override fun didSelectItemAtIndex(adapter: GenericRecyclerAdapter, index: Int) {
            (adapter.snapshot?.snapshotList?.get(index) as? ColorEntity)?.let { item ->

            }
        }
    }

}
