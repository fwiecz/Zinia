package de.hpled.zinia.shows.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import de.hpled.zinia.R
import de.hpled.zinia.shows.interfaces.Show
import de.hpled.zinia.shows.views.ShowPickView
import de.hpled.zinia.shows.views.ShowPickViewAdapter

interface OnShowPickListener {
    fun onShowPick(show: Show)
}

class ShowPickFragment : Fragment() {

    private lateinit var root : LinearLayout
    private val listview by lazy {
        root.findViewById<ListView>(R.id.pickShowListView)
    }
    private val adapter by lazy {
        ShowPickViewAdapter(context!!)
    }

    val onShowPickListener = mutableSetOf<OnShowPickListener>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_pick_show, container, false) as LinearLayout
        return root
    }

    override fun onStart() {
        super.onStart()
        listview.adapter = adapter
        listview.setOnItemClickListener { parent, view, position, id ->
            if(view is ShowPickView) {
                onShowPickListener.forEach { it.onShowPick(view.show) }
            }
        }
    }

    fun setShows(shows: List<Show>) {
        adapter.shows = shows
    }
}