package de.hpled.zinia.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothClass
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import java.lang.IllegalStateException

class DeleteDialogFragment(
    private val device: Device,
    private val onConfirm: (device: Device) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(context, R.style.DefaultAlertDialogStyle).apply {
                setTitle(context.getString(R.string.delete_device_title))
                setMessage(context.getString(R.string.delete_device_text, device.name))
                setPositiveButton(R.string.delete_label, { dialog, which ->
                    onConfirm(device)
                })
                setNegativeButton(R.string.cancel_label, { dialog, which -> })
            }.create()
        } ?: throw IllegalStateException("DeleteDialogFragment activity cannot be null.")
    }
}