package de.hpled.zinia.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.DeviceActivity
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import de.hpled.zinia.newdevice.AddNewDeviceActivity
import de.hpled.zinia.views.DeviceView
import de.hpled.zinia.views.DeviceViewAdapter
import java.util.concurrent.ScheduledThreadPoolExecutor


class ListDevicesFragment : Fragment() {
    private val handler = Handler()
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private lateinit var root: FrameLayout
    private val gridView by lazy { root.findViewById<GridView>(R.id.listDevicesGridView) }
    private val devicesAdapter by lazy {
        DeviceViewAdapter(
            context ?: throw IllegalStateException("ListDevicesFragment has no context initialized")
        )
    }
    private val swipeRefresh by lazy {
        root.findViewById<SwipeRefreshLayout>(R.id.listDevicesSwipeRefresh)
    }
    private val addDeviceButton by lazy {
        root.findViewById<FloatingActionButton>(R.id.addNewDeviceButton)
    }
    private val executor = ScheduledThreadPoolExecutor(5)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_list_devices, container, false) as FrameLayout
        return root
    }

    override fun onStart() {
        super.onStart()
        gridView.apply {
            adapter = devicesAdapter
            setOnItemLongClickListener { parent, view, position, id ->
                initDeleteDialog(view as DeviceView)
                true
            }
            setOnItemClickListener { parent, view, position, id ->
                startDeviceActivity((view as? DeviceView)?.device)
            }
        }
        database.devices.observe(this, Observer { devicesAdapter.devices = it })
        gridView.setOnHierarchyChangeListener(onAttachStateChangeLister)
        swipeRefresh.setOnRefreshListener { testConnectionForAllDevices() }
        addDeviceButton.setOnClickListener {
            val intent = Intent(context, AddNewDeviceActivity::class.java)
            startActivityForResult(intent, NEW_DEVICE_REQUEST_CODE)
        }
    }

    private val onAttachStateChangeLister = object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewRemoved(parent: View?, child: View?) {}
        override fun onChildViewAdded(parent: View?, child: View?) {
            if(child != null && child is DeviceView) {
                executor.execute(child.checkConnectionRunnable())
            }
        }
    }

    private fun testConnectionForAllDevices() {
        swipeRefresh.isRefreshing = true
        gridView.children.forEach {
            if(it is DeviceView) {
                executor.execute(it.checkConnectionRunnable())
            }
        }
        handler.postDelayed({swipeRefresh.isRefreshing = false}, 1000)
    }

    private fun initDeleteDialog(view: DeviceView) {
        fragmentManager?.run {
            DeleteDialogFragment(view.device, {
                database.deleteDevice(it)
            }).show(this, null)
        }
    }

    private fun startDeviceActivity(device: Device?) {
        if(device != null) {
            val intent = Intent(context, DeviceActivity::class.java)
            intent.putExtra(DeviceActivity.INTENT_DEVICE, device)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            NEW_DEVICE_REQUEST_CODE -> { data?.run { database.saveNewDevice(this) } }
        }
    }

    override fun onResume() {
        super.onResume()
        // Force a device list update whenever the fragment is showing.
        AsyncTask.execute {
            val devs = database.findAllDevices()
            handler.post { devicesAdapter.devices = devs}
        }
    }

    companion object {
        private const val NEW_DEVICE_REQUEST_CODE = 0
    }
}
