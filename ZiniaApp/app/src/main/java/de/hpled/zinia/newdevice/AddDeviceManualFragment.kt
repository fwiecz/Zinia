package de.hpled.zinia.newdevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.R
import de.hpled.zinia.views.StatusIndicatorView


class AddDeviceManualFragment : Fragment() {

    private val newDeviceViewModel : NewDeviceViewModel by lazy {
        ViewModelProviders.of(this).get(NewDeviceViewModel::class.java)
    }

    private lateinit var root : View
    private val ipEdit by lazy { root.findViewById<EditText>(R.id.newDeviceIpEditText) }
    private val ipStatus by lazy { root.findViewById<StatusIndicatorView>(R.id.newDeviceIpStatus) }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_add_device_manual, container, false)
        newDeviceViewModel.manualIpStatus.observe(this, Observer { ipStatus.status = it })
        ipEdit.addTextChangedListener { text -> newDeviceViewModel.checkForManualIP(text.toString()) }
        return root
    }
}