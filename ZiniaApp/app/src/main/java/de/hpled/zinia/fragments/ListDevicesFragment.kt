package de.hpled.zinia.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
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
    private val executor = ScheduledThreadPoolExecutor(5)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.list_devices_fragment, container, false) as FrameLayout
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
        }
        database.devices.observe(this, Observer { devicesAdapter.devices = it })
        gridView.setOnHierarchyChangeListener(onAttachStateChangeLister)
        swipeRefresh.setOnRefreshListener { testConnectionForAllDevices() }
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
        AlertDialog.Builder(context, R.style.DefaultAlertDialogStyle).apply {
            setTitle(context.getString(R.string.delete_device_title))
            setMessage(context.getString(R.string.delete_device_text, view.device.name))
            setPositiveButton(R.string.delete_label, { dialog, which ->
                database.deleteDevice(view.device)
            })
            setNegativeButton(R.string.cancel_label, { dialog, which -> })
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
