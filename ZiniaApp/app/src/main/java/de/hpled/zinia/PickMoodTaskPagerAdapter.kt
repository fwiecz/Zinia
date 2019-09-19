package de.hpled.zinia

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.hpled.zinia.fragments.ColorPickerFragment

class PickMoodTaskPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val colorPickerFragment = ColorPickerFragment()

    private val TAB_TITLES = arrayOf(
        R.string.pick_color_label
    )

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> colorPickerFragment
            else -> colorPickerFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount() = 1
}