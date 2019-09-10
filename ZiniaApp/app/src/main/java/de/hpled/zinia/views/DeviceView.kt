package de.hpled.zinia.views

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import de.hpled.zinia.services.DeviceDiscoverService
import java.net.URL

class DeviceView(c: Context, attr: AttributeSet?) : LinearLayout(c, attr) {

    lateinit var device: Device
        private set

    private val ipLabel by lazy { findViewById<TextView>(R.id.device_view_ip_label) }
    private val nameLabel by lazy { findViewById<TextView>(R.id.device_view_name_label) }
    private val numLedLabel by lazy { findViewById<TextView>(R.id.device_view_num_leds_label) }
    private val status by lazy { findViewById<StatusIndicatorView>(R.id.device_view_status_indicator) }
    private val mHandler = Handler()

    init {
        View.inflate(context, R.layout.view_device, this)
    }

    fun setDevice(device: Device) {
        this.device = device
        ipLabel.text = device.ipAddress
        nameLabel.text = device.name
        val leds = device.numLeds
        numLedLabel.text = context.resources.getQuantityString(R.plurals.num_leds_label, leds, leds)
        status.status = StatusIndicatorView.State.UNKNOWN
        checkConnection()
    }

    fun checkConnection() {
        val url = URL("http://${device.ipAddress}/prefs")
        status.status = StatusIndicatorView.State.LOADING
        DeviceDiscoverService.request(url,
            success = {
                mHandler.post { status.status = StatusIndicatorView.State.SUCCESS }
            },
            error = {
                mHandler.post { status.status = StatusIndicatorView.State.ERROR }
            })
    }
}


class DeviceViewAdapter(val context: Context) : BaseAdapter() {

    var devices: List<Device> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val device = devices.get(position)
        if(convertView != null && convertView is DeviceView) {
            convertView.setDevice(device)
            return convertView
        } else {
            val view = DeviceView(context, null)
            view.setDevice(device)
            return view
        }
    }

    override fun getItem(position: Int) = devices.get(position)

    override fun getItemId(position: Int) = devices.get(position).id

    override fun getCount() = devices.size
}