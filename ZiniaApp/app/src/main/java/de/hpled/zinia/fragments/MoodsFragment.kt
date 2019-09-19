package de.hpled.zinia.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.MoodEditorActivity
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.Mood
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.services.HttpRequestService
import de.hpled.zinia.viewmodels.MoodsFragmentViewModel
import de.hpled.zinia.views.MoodView
import de.hpled.zinia.views.MoodViewAdapter
import de.hpled.zinia.views.OnMoodListener
import java.net.URL

class MoodsFragment : Fragment(), OnMoodListener {
    private lateinit var root : FrameLayout
    private val gridView by lazy { root.findViewById<GridView>(R.id.moodsGridView) }
    private val moodAdapter by lazy { MoodViewAdapter(context!!, this) }
    private val addButton by lazy { root.findViewById<FloatingActionButton>(R.id.moodsAddButton) }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(MoodsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_moods, container, false) as FrameLayout
        return root
    }

    override fun onStart() {
        super.onStart()
        gridView.adapter = moodAdapter
        moodAdapter.moodList = listOf()
        addButton.setOnClickListener { createMood() }
        database.moodDao.findAllLiveData().observe(this, Observer {
            moodAdapter.moodList = it
        })
    }

    private fun createMood() {
        val intent = Intent(context, MoodEditorActivity::class.java)
        startActivityForResult(intent, MOOD_EDITOR)
    }

    override fun onEditMood(mood: Mood) {
        val intent = Intent(context, MoodEditorActivity::class.java)
        intent.putExtra(MoodEditorActivity.INTENT_MOOD_ID, mood.id)
        startActivityForResult(intent, MOOD_EDITOR)
    }

    override fun onDeleteMood(mood: Mood) {
        database.deleteMoodAndMoodTasks(mood.id)
    }

    override fun onClickMood(mood: Mood) {
        AsyncTask.execute {
            val moodWithTasks = database.getMoodWithTasks(mood.id)
            moodWithTasks.tasks?.apply {
                viewmodel.playMoodTasks(this)
            }
            if(moodWithTasks.turnOffUnusedDevices) {
                val devices = database.findAllDevices()
                val used = moodWithTasks.tasks?.mapNotNull { it.device } ?: listOf()
                val unused = devices - used
                unused.forEach { viewmodel.turnDeviceOff(it) }
            }
        }
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    companion object {
        private const val MOOD_EDITOR = 1
    }
}