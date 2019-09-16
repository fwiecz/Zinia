package de.hpled.zinia.views

import android.content.Context
import android.graphics.Color
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

    val itemLayout by lazy { findViewById<LinearLayout>(R.id.moodTaskViewItemLayout) }
    val buttonLayout by lazy { findViewById<LinearLayout>(R.id.moodTaskViewButtonLayout) }
    val title by lazy { findViewById<TextView>(R.id.moodTaskViewTitle) }
    val colorView by lazy { findViewById<ImageView>(R.id.moodTaskViewColor) }
    val changeBtn by lazy { findViewById<Button>(R.id.moodTaskViewChangeButton) }

    var active = true
        set(value) {
            field = value
            alpha = if(value)1f else 0.5f
        }

    init {
        View.inflate(context, R.layout.view_mood_task, this)
    }

    fun set(moodTask: MoodTask?) {
        this.moodTask = moodTask
        itemLayout.visibility = if(moodTask == null)View.GONE else View.VISIBLE
        buttonLayout.visibility = if(moodTask == null)View.VISIBLE else View.GONE

        if(moodTask != null) {
            title.text = moodTask.device?.name ?: "-Error-"
            colorView.drawable.setTint(moodTask.color ?: Color.BLACK)
        }
    }
}

class MoodTaskViewAdapter(private val context: Context) : BaseAdapter() {
    var moodTaskList : List<MoodTask?> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val moodTask = moodTaskList.get(position)
        if(convertView != null && convertView is MoodTaskView) {
            return convertView.apply { set(moodTask) }
        }
        else {
            return MoodTaskView(context).apply { set(moodTask) }
        }
    }

    override fun getItem(position: Int) = moodTaskList.get(position)

    override fun getItemId(position: Int) = moodTaskList.get(position)?.id ?: -1

    override fun getCount() = moodTaskList.size
}