package de.hpled.zinia.newdevice

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.hpled.zinia.R
import de.hpled.zinia.fragments.AddDeviceManualFragment
import de.hpled.zinia.fragments.SearchDevicesFragment

class NewDeviceMethodsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val searchDevicesFragment by lazy { SearchDevicesFragment() }
    val addDeviceManualFragment by lazy { AddDeviceManualFragment() }

    private val TAB_TITLES = arrayOf(
        R.string.new_device_tab_text_1,
        R.string.new_device_tab_text_2
    )

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> searchDevicesFragment
            else -> addDeviceManualFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount() = 2
}