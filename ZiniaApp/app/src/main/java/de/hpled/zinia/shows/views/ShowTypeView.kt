package de.hpled.zinia.shows.views

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.hpled.zinia.R

class ShowTypeView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {

    private val icon by lazy { findViewById<ImageView>(R.id.showTypeIcon) }
    private val label by lazy { findViewById<TextView>(R.id.showTypeLabel) }
    var itemId : Int = 0

    init {
        View.inflate(context, R.layout.view_show_type, this)
    }

    fun set(item: MenuItem) {
        label.text = item.title
        icon.setImageDrawable(item.icon)
        itemId = item.itemId
    }
}

class ShowTypeViewAdapter(private val context: Context) : BaseAdapter() {

    var items: List<MenuItem> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = items.get(position)
        if(convertView is ShowTypeView) {
            return convertView.apply { set(item) }
        }
        else {
            return ShowTypeView(context).apply { set(item) }
        }
    }

    override fun getItem(position: Int) = items.get(position)

    override fun getItemId(position: Int) = items.get(position).itemId.toLong()

    override fun getCount() = items.size
}