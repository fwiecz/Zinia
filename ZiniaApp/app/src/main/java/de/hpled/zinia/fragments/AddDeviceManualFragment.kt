package de.hpled.zinia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.R
import de.hpled.zinia.entities.DeviceType
import de.hpled.zinia.newdevice.NewDeviceListener
import de.hpled.zinia.viewmodels.NewDeviceViewModel
import de.hpled.zinia.views.StatusIndicatorView


class AddDeviceManualFragment : Fragment() {

    private val viewModel : NewDeviceViewModel by lazy {
        ViewModelProviders.of(this).get(NewDeviceViewModel::class.java)
    }

    private lateinit var root : View
    private val ipEdit by lazy { root.findViewById<EditText>(R.id.newDeviceIpEditText) }
    private val nameEdit by lazy { root.findViewById<EditText>(R.id.newDeviceNameEditText) }
    private val numLedsEdit by lazy { root.findViewById<EditText>(R.id.newDeviceLedsEditText) }
    private val ipStatus by lazy { root.findViewById<StatusIndicatorView>(R.id.newDeviceIpStatus) }
    private val saveButton by lazy { root.findViewById<Button>(R.id.add_device_manually_button) }
    val newDeviceListener = mutableListOf<NewDeviceListener>()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_add_device_manual, container, false)
        viewModel.manualIpStatus.observe(this, Observer { ipStatus.status = it })
        viewModel.buttonIsClickable.observe(this, Observer {
            saveButton.isEnabled = it
            saveButton.alpha = if(it)1f else 0.5f
        })
        ipEdit.addTextChangedListener { text ->
            viewModel.checkForManualIP(text.toString())
            checkIfFieldsAreValid()
        }
        nameEdit.addTextChangedListener { checkIfFieldsAreValid() }
        numLedsEdit.addTextChangedListener { checkIfFieldsAreValid() }
        saveButton.setOnClickListener { saveDevice() }
        return root
    }

    private fun checkIfFieldsAreValid() {
        viewModel.buttonIsClickable.value = ipEdit.text.matches(viewModel.ipRegex) &&
                nameEdit.text.isNotEmpty() && numLedsEdit.text.isNotEmpty() &&
                (numLedsEdit.text.toString().toIntOrNull() ?: 0) in 1..9999
    }

    private fun saveDevice() {
        val ip = ipEdit.text.toString().trim()
        val name = nameEdit.text.toString().trim()
        val numLeds = numLedsEdit.text.toString().toIntOrNull() ?: 0
        val type = when {
            numLeds == 1 -> DeviceType.SINGLE_LED
            numLeds > 1 -> DeviceType.LED_CHAIN
            else -> DeviceType.UNKNOWN
        }
        newDeviceListener.forEach {
            it.onNewDevice(ip, name, numLeds, type)
        }
    }
}