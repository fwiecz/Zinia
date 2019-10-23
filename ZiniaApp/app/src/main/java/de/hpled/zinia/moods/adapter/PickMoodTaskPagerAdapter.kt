package de.hpled.zinia.moods.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.hpled.zinia.R
import de.hpled.zinia.colorpick.fragments.ColorPickerFragment
import de.hpled.zinia.shows.fragments.ShowPickFragment

class PickMoodTaskPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val colorPickerFragment = ColorPickerFragment()
    val showPickFragment = ShowPickFragment()

    private val TAB_TITLES = arrayOf(
        R.string.pick_color_label,
        R.string.choose_show_label
    )

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> colorPickerFragment
            else -> showPickFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount() = 2
}