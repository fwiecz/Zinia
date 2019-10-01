package de.hpled.zinia.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device

interface OnPreviewControllerActionListener {
    fun onPreviewPlay(device: Device)
    fun onPreviewStop(device: Device)
}

class PreviewControllerViewModel : ViewModel() {
    val isPlaying = MutableLiveData(false)
    val device = MutableLiveData<Device?>()
    var devices = listOf<Device>()
}

class PreviewControllerFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var root: LinearLayout
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(PreviewControllerViewModel::class.java)
    }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val playButton by lazy {
        root.findViewById<ImageButton>(R.id.previewPlay)
    }
    private val stopButton by lazy {
        root.findViewById<ImageButton>(R.id.previewStop)
    }
    private val devicesSpinner by lazy {
        root.findViewById<Spinner>(R.id.previewDeviceSpinner)
    }

    val onPreviewControllerActionListener = mutableSetOf<OnPreviewControllerActionListener>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_preview_controller, container, false)
                as LinearLayout
        return root
    }

    override fun onStart() {
        super.onStart()

        viewmodel.isPlaying.observe(this, Observer {
            playButton.setImageResource(getPlayButtonRes(it))
            stopButton.apply {
                isEnabled = it
                alpha = if (it) 1f else 0.5f
            }
        })
        database.deviceDao.findAllLiveData().observe(this, Observer {
            viewmodel.devices = it
            devicesSpinner.adapter = ArrayAdapter<String>(
                this.context!!,
                R.layout.view_spinner_checked_text,
                it.map { it.name }
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            // if a device was already selected
            viewmodel.device.value?.apply {
                devicesSpinner.setSelection( it.indexOfFirst { it.name == name } )
            }
            viewmodel.device.value = viewmodel.devices.get(devicesSpinner.selectedItemPosition)
        })
        devicesSpinner.onItemSelectedListener = this
        initButtons()
    }

    private fun initButtons() {
        playButton.setOnClickListener {
            if(viewmodel.isPlaying.value == true) { playButton.animate().rotationBy(-360f).start() }
            viewmodel.isPlaying.value = true
            viewmodel.device.value?.apply {
                onPreviewControllerActionListener.forEach { it.onPreviewPlay(this) }
            }
        }
        stopButton.setOnClickListener {
            viewmodel.isPlaying.value = false
            viewmodel.device.value?.apply {
                onPreviewControllerActionListener.forEach { it.onPreviewStop(this) }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val device = viewmodel.devices.getOrNull(position)
        viewmodel.device.value = device
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        viewmodel.device.value = null
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    companion object {
        private fun getPlayButtonRes(isPlaying: Boolean) = when (isPlaying) {
            true -> R.drawable.material_sync
            false -> R.drawable.material_play_arrow
        }
    }
}
