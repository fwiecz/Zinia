package de.hpled.zinia.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import de.hpled.zinia.R

/**
 * Represents brightness status of something with brightness little color_pick_thumb. Four states are available:
 * unknown, loading, onSuccess and onError.
 */
class StatusIndicatorView(c: Context, attr: AttributeSet?) : FrameLayout(c, attr) {

    enum class State {
        UNKNOWN,
        LOADING,
        SUCCESS,
        ERROR
    }

    private val unknownIcon by lazy { findViewById<ImageView>(R.id.statusIndicatorUnknown)}
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.statusIndicatorProgress)}
    private val successIcon by lazy { findViewById<ImageView>(R.id.statusIndicatorSuccess)}
    private val errorIcon by lazy { findViewById<ImageView>(R.id.statusIndicatorError)}
    private val icons by lazy { listOf(progressBar, unknownIcon, successIcon, errorIcon) }

    /**
     * The current status.
     */
    var status = State.UNKNOWN
        set(value) {
            field = value
            showRespectiveIcon(value)
        }

    init {
        View.inflate(context, R.layout.view_status_indicator, this)
        showRespectiveIcon(status)
    }

    private fun showRespectiveIcon(state: State) {
        icons.forEach { it.visibility = View.INVISIBLE}
        when(state) {
            State.UNKNOWN -> unknownIcon.visibility = View.VISIBLE
            State.LOADING -> progressBar.visibility = View.VISIBLE
            State.SUCCESS -> successIcon.visibility = View.VISIBLE
            State.ERROR -> errorIcon.visibility = View.VISIBLE
        }
    }
}