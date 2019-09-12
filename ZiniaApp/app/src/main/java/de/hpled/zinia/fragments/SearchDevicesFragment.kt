package de.hpled.zinia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.hpled.zinia.R
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.viewmodels.AddDeviceManualViewModel
import de.hpled.zinia.viewmodels.SearchDevicesViewModel

/**
 * The Fragment that shows devices in same local network
 */
class SearchDevicesFragment : Fragment(), SearchDevicesViewModel.OnDeviceDiscoveredListener {

    private val viewmodel : SearchDevicesViewModel by lazy {
        ViewModelProviders.of(this).get(SearchDevicesViewModel::class.java)
    }
    private lateinit var root: FrameLayout
    private val swipeRefresh by lazy { root.findViewById<SwipeRefreshLayout>(R.id.discoverDevicesSwipeRefresh) }
    private val listView by lazy { root.findViewById<ListView>(R.id.discoverDevicesListView) }

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
    }

    override fun onDeviceDiscovered(dto: DeviceStatusDTO, ip: String) {
        println("FOUND DEVICE $ip: $dto")
        // TODO display device
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isSearching.value = false
    }
}