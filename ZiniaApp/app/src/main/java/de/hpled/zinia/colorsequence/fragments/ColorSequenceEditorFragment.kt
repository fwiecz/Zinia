package de.hpled.zinia.colorsequence.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hpled.zinia.R
import de.hpled.zinia.colorsequence.views.ColorSequenceCircleView
import de.hpled.zinia.colorsequence.views.OnSegmentClickListener
import de.hpled.zinia.xcolor.Xcolor
import de.hpled.zinia.xcolor.XcolorList
import java.util.*

class ColorSequenceEditorFragmentViewModel : ViewModel() {
    val colors = MutableLiveData<XcolorList>(XcolorList())
    var deleteMode = MutableLiveData<Boolean>(false)
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
    private val deleteButton by lazy {
        root.findViewById<ImageButton>(R.id.colorSequenceDeleteButton)
    }
    private val circle by lazy {
        root.findViewById<ColorSequenceCircleView>(R.id.colorSequenceCircleView)
    }
    val onSegmentClickListener = mutableSetOf<OnSegmentClickListener>()
    var nextColor = Xcolor.random()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(
            R.layout.fragment_color_sequence_editor,
            container,
            false
        ) as FrameLayout
        return root
    }

    override fun onSegmentClick(index: Int, color: Xcolor) {
        if(viewmodel.deleteMode.value == true) {
            viewmodel.colors.value =
                viewmodel.colors.value?.apply { removeAt(index) } ?: XcolorList()
        }
        else {
            onSegmentClickListener.forEach { it.onSegmentClick(index, color) }
        }
    }

    override fun onStart() {
        super.onStart()
        viewmodel.colors.value?.apply {
            if (isEmpty()) {
                addColor()
            }
        }
        viewmodel.colors.value?.apply {
            handler.post { circle.setColorList(this) }
        }
        viewmodel.colors.observe(this, Observer {
            handler.post { circle.setColorList(it) }
            enableAddButton(it.size <= MAX_NUM_COLOR && viewmodel.deleteMode.value == false)
            enableDeleteButton(it.size > 1)
            if(it.size <= 1 && viewmodel.deleteMode.value == true) {
                viewmodel.deleteMode.value = false
            }
        })

        addButton.setOnClickListener { addColor() }
        deleteButton.setOnClickListener { toggleDeleteMode() }
        circle.onSegmentClickListener += this

        viewmodel.deleteMode.observe(this, Observer {
            enableAddButton(!it)
            deleteButton.setImageResource(getDeleteIconResource(it))
            if(it) {
                root.setBackgroundResource(R.drawable.delete_indicator_background)
            } else {
                root.setBackgroundResource(R.color.colorBackground)
            }
        })
    }

    fun setColorAt(index: Int, color: Xcolor) {
        if(index < viewmodel.colors.value?.size ?: 0) {
            viewmodel.colors.value = viewmodel.colors.value?.apply { set(index, color) }
                ?: XcolorList()
        }
    }

    fun setColors(colors: XcolorList) {
        viewmodel.colors.value = colors
    }

    private fun addColor() {
        viewmodel.colors.value = viewmodel.colors.value?.apply { add(nextColor) }
    }

    private fun toggleDeleteMode() {
        viewmodel.deleteMode.value = !(viewmodel.deleteMode.value ?: true)
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

    private fun enableDeleteButton(enable: Boolean) {
        deleteButton.apply {
            isEnabled = enable
            alpha = if (enable) 1f else 0.5f
        }
    }

    fun getColors() = viewmodel.colors.value ?: XcolorList()

    companion object {
        private const val MAX_NUM_COLOR = 24

        private fun getDeleteIconResource(deletemode: Boolean) = when(deletemode) {
            true -> R.drawable.material_done
            false -> R.drawable.material_delete
        }
    }
}
