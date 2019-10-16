package de.hpled.zinia.moods.views

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.hpled.zinia.R
import de.hpled.zinia.entities.MoodTask

class MoodTaskView(c: Context, attr: AttributeSet? = null) : LinearLayout(c, attr) {

    // Nullable so we can distinguish between actual item and the Add-button
    var moodTask: MoodTask? = null
        private set

    private val itemLayout by lazy { findViewById<LinearLayout>(R.id.moodTaskViewItemLayout) }
    private val addButtonLayout by lazy { findViewById<TextView>(R.id.moodTaskViewButtonLayout) }
    private val title by lazy { findViewById<TextView>(R.id.moodTaskViewTitle) }
    private val colorView by lazy { findViewById<ImageView>(R.id.moodTaskViewColor) }

    var active = true
        set(value) {
            field = value
            alpha = if (value) 1f else 0.5f
        }

    init {
        View.inflate(context, R.layout.view_mood_task, this)
        // tint the plus icon
        addButtonLayout.compoundDrawables.mapNotNull { it }.firstOrNull()?.setTint(
            context.resources.getColor(R.color.colorTextTertiary)
        )
    }

    fun set(moodTask: MoodTask?) {
        this.moodTask = moodTask
        itemLayout.visibility = if (moodTask == null) View.GONE else View.VISIBLE
        addButtonLayout.visibility = if (moodTask == null) View.VISIBLE else View.GONE

        if (moodTask != null) {
            title.text = moodTask.device?.name ?: "-Error-"
            colorView.drawable.setColorFilter(moodTask.color ?: Color.BLACK, PorterDuff.Mode.SRC)
            colorView.background.setTint(context.resources.getColor(R.color.colorMoodTaskCircleBackground))
        }
    }
}

class MoodTaskViewAdapter(private val context: Context) :
    BaseAdapter() {
    var moodTaskList: List<MoodTask?> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val moodTask = moodTaskList.get(position)
        if (convertView != null && convertView is MoodTaskView) {
            return convertView.apply { set(moodTask) }
        } else {
            return MoodTaskView(context).apply { set(moodTask) }
        }
    }

    override fun getItem(position: Int) = moodTaskList.get(position)

    override fun getItemId(position: Int) = moodTaskList.get(position)?.id ?: -1

    override fun getCount() = moodTaskList.size
}