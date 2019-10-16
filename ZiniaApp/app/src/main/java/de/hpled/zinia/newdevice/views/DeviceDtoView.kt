package de.hpled.zinia.newdevice.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.hpled.zinia.R
import de.hpled.zinia.dto.DeviceStatusDTO

interface OnAddDeviceDtoListener {
    fun onAddDeviceDto(dto: DeviceStatusDTO)
}

/**
 * This View is used to display new devices found on the same local network.
 */
class DeviceDtoView(c: Context, attr: AttributeSet?) : LinearLayout(c, attr) {

    lateinit var deviceDto : DeviceStatusDTO
        private set

    private val titleLabel by lazy { findViewById<TextView>(R.id.deviceDtoTitle) }
    private val ipLabel by lazy { findViewById<TextView>(R.id.deviceDtoIp) }
    private val addButton by lazy { findViewById<ImageView>(R.id.deviceDtoAddButton) }

    init {
        View.inflate(context, R.layout.view_device_dto, this)
    }

    fun setDeviceDto(dto: DeviceStatusDTO, listener: OnAddDeviceDtoListener) {
        deviceDto = dto
        titleLabel.text = context.resources.getQuantityString(
            R.plurals.num_leds_label,
            dto.numLeds ?: 0,
            dto.numLeds ?: 0
        )
        ipLabel.text = context.getString(R.string.ip_address_label, dto.ip ?: "?")
        addButton.setOnClickListener { listener.onAddDeviceDto(deviceDto) }
    }
}

class DeviceDtoViewAdapter(
    private val onAddDeviceDtoListener: OnAddDeviceDtoListener,
    private val context: Context) : BaseAdapter() {

    var deviceDtoList = listOf<DeviceStatusDTO>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val dto = deviceDtoList.get(position)
        if(convertView != null && convertView is DeviceDtoView) {
            convertView.setDeviceDto(dto, onAddDeviceDtoListener)
            return convertView
        }
        else {
            val view = DeviceDtoView(context, null)
            view.setDeviceDto(dto, onAddDeviceDtoListener)
            return view
        }
    }

    override fun getItem(position: Int): Any = deviceDtoList.get(position)

    override fun getItemId(position: Int): Long = deviceDtoList.get(position).hashCode().toLong()

    override fun getCount(): Int = deviceDtoList.size
}