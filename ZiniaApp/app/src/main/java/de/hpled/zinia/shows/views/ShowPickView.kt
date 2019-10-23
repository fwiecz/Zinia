package de.hpled.zinia.shows.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.hpled.zinia.R
import de.hpled.zinia.shows.interfaces.Show

/**
 * A smaller show view for displaying in list views
 */
class ShowPickView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {

    lateinit var show : Show

    private val icon by lazy { findViewById<ImageView>(R.id.showListViewIcon) }
    private val label by lazy { findViewById<TextView>(R.id.showListViewLabel) }

    init {
        View.inflate(context, R.layout.view_show_pick, this)
    }

    fun set(show: Show) {
        this.show = show
        icon.setImageResource(show.getShowIconRes())
        label.setText(show.getShowName())
    }
}


class ShowPickViewAdapter(private val context: Context) : BaseAdapter() {
    var shows: List<Show> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val show = shows.get(position)
        if(convertView is ShowPickView) {
            return convertView.apply { set(show) }
        } else {
            return ShowPickView(context).apply { set(show) }
        }
    }

    override fun getItem(position: Int) = shows.get(position)

    override fun getItemId(position: Int) = shows.get(position).getShowId()

    override fun getCount() = shows.size
}