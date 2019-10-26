package de.hpled.zinia.shows.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import de.hpled.zinia.R
import de.hpled.zinia.shows.interfaces.OnShowViewListener
import de.hpled.zinia.shows.interfaces.Show

class ShowView(c: Context, attr: AttributeSet? = null) : FrameLayout(c, attr) {
    private lateinit var show: Show
    private val label by lazy { findViewById<TextView>(R.id.showLabel) }
    private val icon by lazy { findViewById<ImageView>(R.id.showIcon) }
    private val gradient by lazy { findViewById<View>(R.id.colorSeqViewGradient) }
    private val menuButton by lazy { findViewById<ImageButton>(R.id.showMenuButton) }
    private val showBody by lazy { findViewById<LinearLayout>(R.id.showBody) }
    private var listener: OnShowViewListener? = null

    init {
        View.inflate(context, R.layout.view_show, this) as FrameLayout
        menuButton.setOnClickListener { openMenu() }
        showBody.setOnClickListener { listener?.onClick(show) }
        showBody.setOnLongClickListener { listener?.onLongClick(show); false}
    }

    private fun setGradient(colors: IntArray) {
        gradient.apply {
            setBackgroundResource(R.drawable.card_background_large_corners)
            val shape = (background as GradientDrawable).let { it.mutate() as GradientDrawable }
            shape.colors = colors.map { Color.rgb(it.red, it.green, it.blue) }.toIntArray()
            shape.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        }
    }

    private fun openMenu() {
        val popMenu = PopupMenu(context, menuButton).apply {
            menuInflater.inflate(R.menu.edit_delete_menu, menu)
            setOnMenuItemClickListener(onMenuItemListener)
        }
        MenuPopupHelper(context, popMenu.menu as MenuBuilder, menuButton).apply {
            setForceShowIcon(true)
            show()
        }
    }

    private val onMenuItemListener = object : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item?.itemId) {
                R.id.menu_edit -> listener?.onEdit(show)
                R.id.menu_delete -> listener?.onDelete(show)
            }
            return false
        }
    }

    fun set(show: Show, listener: OnShowViewListener) {
        this.show = show
        this.listener = listener
        label.text = show.getShowName()
        icon.setImageResource(show.getShowIconRes())
        show.getBackgroundGradientValues()?.apply {
            setGradient(this)
        }
    }
}

