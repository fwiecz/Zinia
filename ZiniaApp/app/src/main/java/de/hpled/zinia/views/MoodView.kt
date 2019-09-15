package de.hpled.zinia.views

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import de.hpled.zinia.R
import de.hpled.zinia.entities.Mood

interface OnMoodEditListener {
    fun onEditMood(mood: Mood)
    fun onDeleteMood(mood: Mood)
}

class MoodView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {
    lateinit var mood: Mood
    private var listener: OnMoodEditListener? = null
    val menuButton by lazy { findViewById<ImageButton>(R.id.moodViewMenuButton) }
    val title by lazy { findViewById<TextView>(R.id.moodViewTitle) }

    init {
        View.inflate(context, R.layout.view_mood, this)
        menuButton.setOnClickListener { onMenuButton(it as ImageButton) }
    }

    private fun onMenuButton(view: ImageButton) {
        val popMenu = PopupMenu(context, view).apply {
            menuInflater.inflate(R.menu.mood_menu, menu)
            setOnMenuItemClickListener(onMenuShowListener)
        }
        MenuPopupHelper(context, popMenu.menu as MenuBuilder, view).apply {
            setForceShowIcon(true)
            show()
        }
    }

    private val onMenuShowListener = object : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item?.itemId) {
                R.id.moode_menu_delete -> listener?.onDeleteMood(mood)
            }
            return true
        }
    }

    fun set(mood: Mood, listener: OnMoodEditListener) {
        this.mood = mood
        title.text = mood.name
        this.listener = listener
    }
}

class MoodViewAdapter(private val context: Context, private val listener: OnMoodEditListener) :
    BaseAdapter() {

    var moodList: List<Mood> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mood = moodList.get(position)
        if (convertView != null && convertView is MoodView) {
            return convertView.apply { set(mood, listener) }
        } else {
            return MoodView(context).apply { set(mood, listener) }
        }
    }

    override fun getItem(position: Int) = moodList.get(position)

    override fun getItemId(position: Int) = moodList.get(position).id

    override fun getCount() = moodList.size
}