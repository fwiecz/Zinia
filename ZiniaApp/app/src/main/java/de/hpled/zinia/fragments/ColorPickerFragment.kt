package de.hpled.zinia.fragments


import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.SweepGradient
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

import de.hpled.zinia.R

/**
 * The User can intuitively pick a color.
 */
class ColorPickerFragment : Fragment() {

    private lateinit var root: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_color_picker, container, false) as ConstraintLayout
        return root
    }
}
