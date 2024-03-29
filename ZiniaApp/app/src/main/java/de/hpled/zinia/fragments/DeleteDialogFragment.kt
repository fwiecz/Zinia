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
    private val title: String,
    private val message: String,
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(context, R.style.DefaultAlertDialogStyle).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton(R.string.delete_label, { dialog, which ->
                    onConfirm()
                })
                setNegativeButton(R.string.cancel_label, { dialog, which -> })
            }.create()
        } ?: throw IllegalStateException("DeleteDialogFragment activity cannot be null.")
    }
}