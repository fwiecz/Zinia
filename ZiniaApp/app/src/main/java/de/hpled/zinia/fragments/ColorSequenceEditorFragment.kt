package de.hpled.zinia.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton

import de.hpled.zinia.R
import de.hpled.zinia.views.ColorSequenceCircleView
import de.hpled.zinia.views.OnSegmentClickListener
import java.util.*

class ColorSequenceEditorFragmentViewModel : ViewModel() {
    val colors = MutableLiveData<List<Int>>(listOf())
}

class ColorSequenceEditorFragment : Fragment(), OnSegmentClickListener {
    private val handler = Handler()
    private lateinit var root: FrameLayout
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(ColorSequenceEditorFragmentViewModel::class.java)
    }
    private val addButton by lazy {
        root.findViewById<FloatingActionButton>(R.id.colorSequenceAddButton)
    }
    private val circle by lazy {
        root.findViewById<ColorSequenceCircleView>(R.id.colorSequenceCircleView)
    }
    private val rand = Random()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(
            R.layout.fragment_color_sequence_editor,
            container,
            false
        ) as FrameLayout
        circle.onSegmentClickListener += this
        return root
    }

    override fun onStart() {
        super.onStart()
        viewmodel.colors.value?.apply {
            if (isEmpty()) {
                addRandomColor()
            }
        }
        viewmodel.colors.value?.apply {
            handler.post { circle.setColorList(this) }
        }
        viewmodel.colors.observe(this, Observer {
            handler.post { circle.setColorList(it) }
            enableAddButton(it.size <= MAX_NUM_COLOR)
        })

        addButton.setOnClickListener { addRandomColor() }
    }

    override fun onSegmentClick(index: Int, color: Int) {

    }

    private fun addRandomColor() {
        viewmodel.colors.value =
            (viewmodel.colors.value ?: listOf()) +
                    listOf(
                        Color.HSVToColor(
                            floatArrayOf(rand.nextFloat() * 360, 1f, 1f)
                        )
                    )
    }

    private fun enableAddButton(enable: Boolean) {
        addButton.apply {
            isEnabled = enable
            alpha = if (enable) 1f else 0.5f
            backgroundTintList = ColorStateList.valueOf(
                if (enable) {
                    resources.getColor(R.color.colorAccent)
                } else {
                    resources.getColor(R.color.colorStateUnknown)
                }
            )
        }
    }

    companion object {
        private const val MAX_NUM_COLOR = 24
    }
}
