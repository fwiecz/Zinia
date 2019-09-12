package de.hpled.zinia.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.hpled.zinia.R
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.DeviceType
import de.hpled.zinia.newdevice.NewDeviceListener
import de.hpled.zinia.viewmodels.SearchDevicesViewModel
import de.hpled.zinia.views.DeviceDtoViewAdapter
import de.hpled.zinia.views.OnAddDeviceDtoListener

/**
 * The Fragment that shows devices in same local network
 */
class SearchDevicesFragment : Fragment(),
    SearchDevicesViewModel.OnDeviceDiscoveredListener,
    OnAddDeviceDtoListener
{
    private val viewmodel : SearchDevicesViewModel by lazy {
        ViewModelProviders.of(this).get(SearchDevicesViewModel::class.java)
    }
    private lateinit var root: FrameLayout
    private val swipeRefresh by lazy { root.findViewById<SwipeRefreshLayout>(R.id.discoverDevicesSwipeRefresh) }
    private val listView by lazy { root.findViewById<ListView>(R.id.discoverDevicesListView) }
    private val adapter by lazy {
        DeviceDtoViewAdapter(this, context ?:
        throw IllegalStateException("SearchDevicesFragments context has not been initialized."))
    }
    val newDeviceListener = mutableListOf<NewDeviceListener>()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_search_devices, container, false) as FrameLayout
        return root
    }

    override fun onStart() {
        super.onStart()
        swipeRefresh.setOnRefreshListener { viewmodel.searchForDevices() }
        viewmodel.onDeviceDiscoveredListener += this
        viewmodel.isSearching.observe(this, Observer { swipeRefresh.isRefreshing = it })
        viewmodel.discoveredDevices.observe(this, Observer { adapter.deviceDtoList = it })
        listView.adapter = adapter
    }

    override fun onDeviceDiscovered(dto: DeviceStatusDTO, ip: String) {
        dto.ip = ip
        val ls = (viewmodel.discoveredDevices.value as List<DeviceStatusDTO>) + dto
        viewmodel.discoveredDevices.value = ls
    }

    private fun informNewDeviceListener(dto: DeviceStatusDTO, name: String) {
        val leds = dto.numLeds ?: 0
        newDeviceListener.forEach { it.onNewDevice(
            dto.ip ?: "",
            name.trim(),
            dto.numLeds ?: 0,
            if(leds == 1)DeviceType.SINGLE_LED else if(leds > 1)DeviceType.LED_CHAIN
            else DeviceType.UNKNOWN
        ) }
    }

    override fun onAddDeviceDto(dto: DeviceStatusDTO) {
        fragmentManager?.apply {
            NewDeviceNameDialogFragment(dto, ::informNewDeviceListener).show(this, null)
        }
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isSearching.value = false
    }
}