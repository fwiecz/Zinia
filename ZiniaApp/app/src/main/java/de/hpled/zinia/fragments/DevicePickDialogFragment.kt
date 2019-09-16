package de.hpled.zinia.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import de.hpled.zinia.views.DevicePickViewAdapter

interface OnDevicePickListener {
    fun onDevicePicked(device: Device)
}

class DevicePickDialogFragment(
    private val devicesList: List<Device>,
    private val listener: OnDevicePickListener?
) : DialogFragment() {

    private val adapter by lazy { DevicePickViewAdapter(context!!) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialog = AlertDialog.Builder(it, R.style.DefaultAlertDialogStyle).apply {
                setTitle(getString(R.string.choose_device_label))
                if (devicesList.isNotEmpty()) {
                    setAdapter(adapter) { dialog, which ->
                        listener?.onDevicePicked(devicesList.get(which))
                    }
                }
            }.create()
            adapter.devices = devicesList
            dialog
        }!!
    }
}