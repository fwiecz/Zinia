package de.hpled.zinia.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.core.view.children
import de.hpled.zinia.R

class ChooseShowTypeView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {

    private val grid by lazy { findViewById<GridView>(R.id.chooseShowGridView) }
    private val adapter = ShowTypeViewAdapter(context)

    init {
        View.inflate(context, R.layout.view_choose_show_type, this)

        // Use this [PopMenu] only to access the [MenuItem]s
        val p = PopupMenu(context, this)
        p.inflate(R.menu.choose_show_type_menu)
        grid.adapter = adapter
        adapter.items = p.menu.children.toList()

        grid.setOnItemClickListener { parent, view, position, id ->
            when((view as ShowTypeView).itemId) {
                R.id.show_type_color_sqeuence -> { }
            }
        }
    }
}