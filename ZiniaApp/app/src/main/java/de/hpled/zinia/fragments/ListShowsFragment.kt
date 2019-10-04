package de.hpled.zinia.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hpled.zinia.colorsequence.ColorSequenceEditorActivity
import de.hpled.zinia.viewmodels.ListShowsViewModel
import de.hpled.zinia.R
import de.hpled.zinia.views.ChooseShowTypeView
import de.hpled.zinia.views.OnChooseShowTypeListener


class ListShowsFragment : Fragment(), OnChooseShowTypeListener {
    private lateinit var root: FrameLayout
    private val viewModel: ListShowsViewModel by lazy {
        ViewModelProviders.of(this).get(ListShowsViewModel::class.java)
    }
    private val addButton by lazy {
        root.findViewById<FloatingActionButton>(R.id.addNewShowButton)
    }
    private val chooseType by lazy {
        root.findViewById<ChooseShowTypeView>(R.id.chooseShowTypeView)
    }
    private var chooseTypeIsVisible = false
    private val colorAccent by lazy { context!!.resources.getColor(R.color.colorAccent) }
    private val colorGrey by lazy { context!!.resources.getColor(R.color.colorStateUnknown) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_list_shows, container, false) as FrameLayout
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        chooseType.onChooseShowTypeListener += this
        addButton.setOnClickListener {
            chooseTypeIsVisible = !chooseTypeIsVisible
            chooseType.toggle(chooseTypeIsVisible)
            toggleFloatingButton(!chooseTypeIsVisible)
        }
    }

    override fun onChooseShowType(itemId: Int) {
        closeChooseMenu()
        when(itemId) {
            R.id.show_type_color_sqeuence -> {
                val intent = Intent(context, ColorSequenceEditorActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun toggleFloatingButton(isDefault: Boolean) {
        if (isDefault) {
            addButton.animate().rotation(0f).start()
            animateAddButton(colorGrey, colorAccent)
        } else {
            addButton.animate().rotation(135f).start()
            animateAddButton(colorAccent, colorGrey)
        }
    }

    private fun animateAddButton(colorFrom: Int, colorTo: Int) {
        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
            duration = ChooseShowTypeView.TRANSITION_DURATION
            addUpdateListener {
                val ls = ColorStateList(arrayOf(intArrayOf()), intArrayOf(it.animatedValue as Int))
                addButton.backgroundTintList = ls
            }
            start()
        }
    }

    private fun closeChooseMenu() {
        chooseTypeIsVisible = false
        chooseType.hide()
        toggleFloatingButton(true)
    }
}
