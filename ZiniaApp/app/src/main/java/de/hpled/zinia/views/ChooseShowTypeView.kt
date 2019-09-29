package de.hpled.zinia.views

import android.animation.*
import android.app.Activity
import android.content.Context
import android.renderscript.Sampler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.core.view.children
import de.hpled.zinia.R
import kotlin.math.max

interface OnChooseShowTypeListener {
    fun onChooseShowType(itemId: Int)
}

class ChooseShowTypeView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {

    private val grid by lazy { findViewById<GridView>(R.id.chooseShowGridView) }
    private val adapter = ShowTypeViewAdapter(context)
    private val paramHidden = LayoutParams(LayoutParams.MATCH_PARENT, 0)
    private val paramShown = LayoutParams(LayoutParams.MATCH_PARENT, 1)

    private var targetHeight = 0
    private var currentAnimator: ValueAnimator? = null

    val onChooseShowTypeListener = mutableSetOf<OnChooseShowTypeListener>()

    init {
        View.inflate(context, R.layout.view_choose_show_type, this)

        // Use this [PopMenu] only to access the [MenuItem]s
        val p = PopupMenu(context, this)
        p.inflate(R.menu.choose_show_type_menu)
        grid.adapter = adapter
        adapter.items = p.menu.children.toList()

        grid.setOnItemClickListener { parent, view, position, id ->
            if(view is ShowTypeView) {
                onChooseShowTypeListener.forEach { it.onChooseShowType(view.itemId) }
            }
        }

        // save the actual height
        post {
            targetHeight = height
            layoutParams = paramHidden
        }
    }


    fun show() {
        currentAnimator?.cancel()
        currentAnimator = ValueAnimator.ofInt(height, targetHeight).apply {
            duration = TRANSITION_DURATION
            interpolator = interpolateShow
            addUpdateListener {
                layoutParams = paramShown.apply { height = it.animatedValue as Int }
            }
            start()
        }
    }

    fun hide() {
        currentAnimator?.cancel()
        currentAnimator = ValueAnimator.ofInt(height, 0).apply {
            duration = TRANSITION_DURATION
            interpolator = interpolateHide
            addUpdateListener {
                layoutParams = paramShown.apply { height = it.animatedValue as Int }
            }
            start()
        }
    }

    fun toggle(isVisible: Boolean) {
        if(isVisible) {
            show()
        } else {
            hide()
        }
    }

    companion object {
        const val TRANSITION_DURATION = 400L
        private val interpolateShow = OvershootInterpolator(2f)
        private val interpolateHide = AccelerateDecelerateInterpolator()
    }
}