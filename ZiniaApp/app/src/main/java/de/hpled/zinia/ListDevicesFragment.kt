package de.hpled.zinia

import android.bluetooth.BluetoothClass
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.lifecycle.Observer
import de.hpled.zinia.entities.Device
import de.hpled.zinia.views.DeviceViewAdapter
import java.lang.IllegalStateException


class ListDevicesFragment : Fragment(), DevicesListChangedListener {

    val viewModel by lazy { ViewModelProviders.of(this).get(ListDevicesViewModel::class.java) }

    val devicesAdapter by lazy {
        DeviceViewAdapter(context ?:
        throw IllegalStateException("ListDevicesFragment has no context initialized"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.list_devices_fragment, container, false)
        root.findViewById<GridView>(R.id.listDevicesGridView).adapter = devicesAdapter
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.devices.observe(this, Observer { devicesAdapter.devices = it })
    }

    override fun onDevicesChanged(devices: List<Device>) {
        viewModel.devices.value = devices
    }
}
