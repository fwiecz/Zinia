package de.hpled.zinia.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import de.hpled.zinia.R
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.views.DeviceDtoView
import java.lang.IllegalStateException


class NewDeviceNameDialogFragment(
    private val dto: DeviceStatusDTO,
    private val onSave: (DeviceStatusDTO, String) -> Unit
) : DialogFragment() {

    private val contentView by lazy {
        LinearLayout(context).also {
            View.inflate(context, R.layout.view_dialog_enter_name, it)
        }
    }

    private val nameEdit by lazy {
        contentView.findViewById<EditText>(R.id.dialogEnterNameEditText)
    }

    private lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialog = AlertDialog.Builder(it, R.style.DefaultAlertDialogStyle).run {
                setTitle(getString(R.string.enter_name_label))
                setView(contentView)
                setNegativeButton(getString(R.string.cancel_label)) { d, w -> }
                setPositiveButton(getString(R.string.save_device_button_label)) { d, w ->
                    if (nameEdit.text.isNotEmpty()) {
                        onSave(dto, nameEdit.text.toString())
                    }
                }
                create()
            }
            dialog.also { this.dialog = it }
        } ?: throw IllegalStateException("NewDeviceDialogFragment activit cannot be null.")
    }

    private fun checkIfNameIsValid() {
        val textValid = nameEdit.text.isNotBlank()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            isEnabled = textValid
            alpha = if (textValid) 1f else 0.5f
        }
    }

    override fun onStart() {
        super.onStart()
        nameEdit.addTextChangedListener { checkIfNameIsValid() }
        checkIfNameIsValid()
    }
}