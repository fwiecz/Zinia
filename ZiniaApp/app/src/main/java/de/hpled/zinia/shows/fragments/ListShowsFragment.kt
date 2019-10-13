package de.hpled.zinia.shows.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.colorsequence.ColorSequenceEditorActivity
import de.hpled.zinia.entities.Device
import de.hpled.zinia.fragments.DeleteDialogFragment
import de.hpled.zinia.fragments.DevicePickDialogFragment
import de.hpled.zinia.fragments.OnDevicePickListener
import de.hpled.zinia.shows.adapters.ShowViewAdapter
import de.hpled.zinia.shows.interfaces.OnShowViewListener
import de.hpled.zinia.shows.interfaces.Show
import de.hpled.zinia.shows.interfaces.ShowType
import de.hpled.zinia.views.ChooseShowTypeView
import de.hpled.zinia.views.OnChooseShowTypeListener
import java.util.concurrent.ScheduledThreadPoolExecutor

class ShowsViewModel : ViewModel() {

    private val executor = ScheduledThreadPoolExecutor(1)

    fun getEditorIntentForShow(context: Context, show: Show) : Intent {
        return Intent(context, when(show.getShowType()) {
            ShowType.COLOR_SEQUENCE -> ColorSequenceEditorActivity::class.java
        }).apply {
            when(show.getShowType()) {
                ShowType.COLOR_SEQUENCE -> {
                    putExtra(ColorSequenceEditorActivity.INTENT_COLOR_SEQ_ID, show.getShowId())
                }
            }
        }
    }

    fun playShow(show: Show, device: Device) {
        executor.execute(show.getSendingJob(device.ipAddress))
    }
}

class ListShowsFragment : Fragment(), OnChooseShowTypeListener, OnShowViewListener {
    private lateinit var root: FrameLayout
    private val viewModel: ShowsViewModel by lazy {
        ViewModelProviders.of(this).get(ShowsViewModel::class.java)
    }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val addButton by lazy {
        root.findViewById<FloatingActionButton>(R.id.addNewShowButton)
    }
    private val chooseType by lazy {
        root.findViewById<ChooseShowTypeView>(R.id.chooseShowTypeView)
    }
    private val gridView by lazy {
        root.findViewById<GridView>(R.id.listShowsGridView)
    }
    private val adapter by lazy {
        ShowViewAdapter(context!!, this)
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

    override fun onStart() {
        super.onStart()
        gridView.adapter = adapter
        gridView.setOnItemClickListener(null)
        database.colorSequenceDao.findAllLiveData().observe(this, Observer {
            adapter.shows = it
        })
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

    override fun onClick(show: Show) {
        AsyncTask.execute {
            val devices = database.findAllDevices()
            DevicePickDialogFragment(devices, object : OnDevicePickListener {
                override fun onDevicePicked(device: Device) {
                    viewModel.playShow(show, device)
                }
            }).show(childFragmentManager, null)
        }
    }

    override fun onLongClick(show: Show) {
    }

    override fun onEdit(show: Show) {
        val intent = viewModel.getEditorIntentForShow(context!!, show)
        startActivity(intent)
    }

    override fun onDelete(show: Show) {
        val ddialog = DeleteDialogFragment(
            getString(R.string.delete_show_label),
            getString(R.string.delete_text, show.getShowName()),
            { database.deleteShow(show) }
        )
        ddialog.show(childFragmentManager, null)
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

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }
}
