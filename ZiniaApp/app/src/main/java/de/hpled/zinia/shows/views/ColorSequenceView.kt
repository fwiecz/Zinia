package de.hpled.zinia.shows.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.setMargins
import de.hpled.zinia.R
import de.hpled.zinia.entities.ColorSequence

class ColorSequenceView(c: Context, attr: AttributeSet? = null) : FrameLayout(c, attr) {
    private lateinit var colorSequence: ColorSequence
    private val label by lazy { findViewById<TextView>(R.id.colorSeqViewLabel) }
    private val gradient by lazy { findViewById<View>(R.id.colorSeqViewGradient) }

    init {
        View.inflate(context, R.layout.view_color_sequence, this) as FrameLayout
    }

    private fun setGradient(colors: IntArray) {
        gradient.apply {
            setBackgroundResource(R.drawable.card_background)
            val shape = (background as GradientDrawable).let { it.mutate() as GradientDrawable }
            shape.colors = colors
            shape.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        }
    }

    fun set(seq: ColorSequence) {
        colorSequence = seq
        label.text = seq.name
        setGradient(seq.colors)
    }
}

class ColorSequenceViewAdapter(private val context: Context) : BaseAdapter() {

    var colorSequences : List<ColorSequence> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val seq = colorSequences.get(position)
        if(convertView is ColorSequenceView) {
            return convertView.apply { set(seq) }
        }
        else {
            return ColorSequenceView(context).apply { set(seq) }
        }
    }

    override fun getItem(position: Int) = colorSequences.get(position)

    override fun getItemId(position: Int) = colorSequences.get(position).id

    override fun getCount() = colorSequences.size
}