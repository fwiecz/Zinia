package de.hpled.zinia.shows.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import de.hpled.zinia.shows.interfaces.OnShowViewListener
import de.hpled.zinia.shows.interfaces.Show
import de.hpled.zinia.shows.views.ShowView

class ShowViewAdapter(
    private val context: Context,
    private val listener: OnShowViewListener
) : BaseAdapter() {

    var shows: List<Show> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val s = shows.get(position)
        if (convertView is ShowView) {
            return convertView.apply { set(s, listener) }
        } else {
            return ShowView(context).apply { set(s, listener) }
        }
    }

    override fun getItem(position: Int) = shows.get(position)

    override fun getItemId(position: Int) = shows.get(position).getShowId()

    override fun getCount() = shows.size
}