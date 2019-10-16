package de.hpled.zinia.devices.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.hpled.zinia.R
import de.hpled.zinia.Resources
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.DeviceType

class DevicePickView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {

    lateinit var device: Device
        private set

    private val label by lazy { findViewById<TextView>(R.id.pick_device_name) }
    private val ipLabel by lazy { findViewById<TextView>(R.id.pick_device_ip) }
    private val icon by lazy { findViewById<ImageView>(R.id.pick_device_icon) }

    init {
        View.inflate(context, R.layout.view_device_pick, this)
    }

    fun set(device: Device) {
        this.device = device
        label.text = device.name
        ipLabel.text = device.ipAddress
        icon.setImageDrawable(getIcon(device.type))
    }

    private fun getIcon(type: DeviceType) : Drawable? {
        val id = Resources.getDeviceIconId(type)
        return if(id != null)context.getDrawable(id) else null
    }
}

class DevicePickViewAdapter(private val context: Context) : BaseAdapter() {
    var devices: List<Device> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val device = devices.get(position)
        if(convertView != null && convertView is DevicePickView) {
            return convertView.apply { set(device) }
        }
        else {
            return DevicePickView(context).apply { set(device) }
        }
    }

    override fun getItem(position: Int) = devices.get(position)

    override fun getItemId(position: Int) = devices.get(position).id

    override fun getCount() = devices.size
}