package de.hpled.zinia.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.lifecycle.Observer
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import de.hpled.zinia.views.DeviceView
import de.hpled.zinia.views.DeviceViewAdapter
import java.lang.IllegalStateException


class ListDevicesFragment : Fragment() {

    val database by lazy { ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java) }

    val devicesAdapter by lazy {
        DeviceViewAdapter(context ?:
        throw IllegalStateException("ListDevicesFragment has no context initialized"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.list_devices_fragment, container, false)
        root.findViewById<GridView>(R.id.listDevicesGridView).apply {
            adapter = devicesAdapter
            setOnItemLongClickListener { parent, view, position, id ->
                initDeleteDialog(view as DeviceView)
                true
            }
        }
        database.devices.observe(this, Observer { devicesAdapter.devices = it })
        return root
    }

    private fun initDeleteDialog(view: DeviceView) {
        AlertDialog.Builder(context, R.style.DefaultAlertDialogStyle).apply {
            setTitle(context.getString(R.string.delete_device_title))
            setMessage(context.getString(R.string.delete_device_text, view.device.name))
            setPositiveButton(R.string.delete_label, { dialog, which ->
                database.deleteDevice(view.device)
            })
            setNegativeButton(R.string.cancel_label, { dialog, which ->  })
            create()
            show()
        }
    }

    /**
     * Updates the devices list by force.
     */
    fun updateDevices(list: List<Device>) {
        devicesAdapter.devices = list
    }
}
