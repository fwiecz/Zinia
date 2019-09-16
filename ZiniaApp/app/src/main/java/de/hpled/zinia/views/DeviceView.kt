package de.hpled.zinia.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.hpled.zinia.R
import de.hpled.zinia.Resources
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.DeviceType
import de.hpled.zinia.services.HttpRequestService
import java.net.URL

class DeviceView(c: Context, attr: AttributeSet?) : LinearLayout(c, attr) {

    lateinit var device: Device
        private set

    private val ipLabel by lazy { findViewById<TextView>(R.id.device_view_ip_label) }
    private val nameLabel by lazy { findViewById<TextView>(R.id.device_view_name_label) }
    private val numLedLabel by lazy { findViewById<TextView>(R.id.device_view_num_leds_label) }
    private val status by lazy { findViewById<StatusIndicatorView>(R.id.device_view_status_indicator) }
    private val icon by lazy { findViewById<ImageView>(R.id.device_view_icon) }
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
        icon.setImageDrawable(getIcon(device.type))
    }

    fun checkConnectionRunnable() : Runnable {
        val url = URL("http://${device.ipAddress}/")
        status.status = StatusIndicatorView.State.LOADING
        return HttpRequestService.requestToRunnable<DeviceStatusDTO>(url,
            success = {
                mHandler.post { status.status = StatusIndicatorView.State.SUCCESS }
            },
            error = {
                mHandler.post { status.status = StatusIndicatorView.State.ERROR }
            },
            responseType = DeviceStatusDTO::class.java
        )
    }

    private fun getIcon(type: DeviceType) : Drawable? {
        val id = Resources.getDeviceIconId(type)
        return if(id != null)context.getDrawable(id) else null
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