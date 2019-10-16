package de.hpled.zinia.colorsequence.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.hpled.zinia.colorsequence.fragments.ColorSequenceEditorFragment
import de.hpled.zinia.colorpick.fragments.ColorPickerFragment

class ColorsequenceEditorPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val colorSequenceEditorFragment = ColorSequenceEditorFragment()
    val colorPickerFragment = ColorPickerFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> colorSequenceEditorFragment
            else -> colorPickerFragment
        }
    }

    override fun getCount() = 2
}