package de.hpled.zinia.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.hpled.zinia.PickMoodTaskPagerAdapter
import de.hpled.zinia.R
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.viewmodels.PickMoodTaskDialogViewModel

interface OnPickMoodTaskListener {
    fun onPickMoodTask(task: MoodTask)
}

class PickMoodTaskDialogFragment : DialogFragment(), ColorPickerFragment.OnColorChangedListener {

    private lateinit var root: View
    private val handler = Handler()
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(PickMoodTaskDialogViewModel::class.java)
    }

    lateinit var task: MoodTask
    val listener: MutableList<OnPickMoodTaskListener> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenAlertDialog)
        savedInstanceState?.apply {
            task = getSerializable(SBUNDLE_TASK) as MoodTask
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.dialog_pick_mood_task, container)
        root.findViewById<Toolbar>(R.id.pickMoodTaskToolbar)?.apply {
            title = getString(R.string.pick_moodtask_dialog_title, task.device?.name)
            navigationIcon?.setTint(context!!.resources.getColor(R.color.colorTextPrimary))
            setNavigationOnClickListener { this@PickMoodTaskDialogFragment.dismiss() }
            inflateMenu(R.menu.mood_task_menu)
            setOnMenuItemClickListener(onMenuLisstener)
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        val pagerAdapter = PickMoodTaskPagerAdapter(context!!, childFragmentManager)
        val pager = root.findViewById<ViewPager>(R.id.pickMoodTaskViewPager)
        val tabs = root.findViewById<TabLayout>(R.id.pickMoodTaskTabs)
        viewmodel.colorSendingService.targetIP = task.device?.ipAddress ?: ""
        pagerAdapter.colorPickerFragment.onColorChangedListener += listOf(
            viewmodel.colorSendingService, this
        )
        handler.post {
            pager.adapter = pagerAdapter
            tabs.setupWithViewPager(pager)
        }
    }

    private val onMenuLisstener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.mood_task_done -> onClickDone()
            }
            return false
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewmodel.colorSendingService.stopExecutor()
    }

    private fun onClickDone() {
        listener.forEach { it.onPickMoodTask(task) }

        dismiss()
    }

    override fun onColorChanged(color: Int, final: Boolean) {
        if (final) {
            task.color = color
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SBUNDLE_TASK, task)
    }

    companion object {
        private const val SBUNDLE_TASK = "SBUNDLE_TASK"
    }
}