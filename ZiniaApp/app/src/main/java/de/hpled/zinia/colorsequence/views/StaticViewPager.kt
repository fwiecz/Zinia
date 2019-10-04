package de.hpled.zinia.colorsequence.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * This ViewPager can only be controlled via software.
 * User swiping will not change its current page.
 */
class StaticViewPager(c: Context, attr: AttributeSet? = null) : ViewPager(c, attr) {

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(ev).let { false }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun scrollBy(x: Int, y: Int) {}
}