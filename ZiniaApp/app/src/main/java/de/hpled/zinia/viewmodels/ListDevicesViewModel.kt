package de.hpled.zinia.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.entities.Device

class ListDevicesViewModel : ViewModel() {
    val devices = MutableLiveData<List<Device>>(listOf())
}
