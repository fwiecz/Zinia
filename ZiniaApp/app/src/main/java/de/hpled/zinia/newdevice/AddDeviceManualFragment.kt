package de.hpled.zinia.newdevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.R

/**
 * A placeholder fragment containing a simple view.
 */
class AddDeviceManualFragment : Fragment() {

    private val newDeviceViewModel : NewDeviceViewModel by lazy {
        ViewModelProviders.of(this).get(NewDeviceViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_add_device_manual, container, false)

        return root
    }

}