package de.hpled.zinia.moods.views

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

interface OnMoodListener {
    fun onEditMood(mood: Mood)
    fun onDeleteMood(mood: Mood)
    fun onClickMood(mood: Mood)
}

class MoodView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {
    lateinit var mood: Mood
    private var listener: OnMoodListener? = null
    val menuButton by lazy { findViewById<ImageButton>(R.id.moodViewMenuButton) }
    val title by lazy { findViewById<TextView>(R.id.moodViewTitle) }

    init {
        val root = View.inflate(context, R.layout.view_mood, this)
        menuButton.setOnClickListener { onMenuButton(it as ImageButton) }
        root.setOnClickListener { listener?.onClickMood(mood) }
    }

    private fun onMenuButton(view: ImageButton) {
        val popMenu = PopupMenu(context, view).apply {
            menuInflater.inflate(R.menu.edit_delete_menu, menu)
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
                R.id.menu_delete -> listener?.onDeleteMood(mood)
                R.id.menu_edit -> listener?.onEditMood(mood)
            }
            return true
        }
    }

    fun set(mood: Mood, listener: OnMoodListener) {
        this.mood = mood
        title.text = mood.name
        this.listener = listener
    }
}

class MoodViewAdapter(private val context: Context, private val listener: OnMoodListener) :
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