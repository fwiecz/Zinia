package de.hpled.zinia.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
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
import de.hpled.zinia.entities.Mood
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.views.MoodView
import de.hpled.zinia.views.MoodViewAdapter
import de.hpled.zinia.views.OnMoodEditListener

class MoodsFragment : Fragment(), OnMoodEditListener {
    private lateinit var root : FrameLayout
    private val gridView by lazy { root.findViewById<GridView>(R.id.moodsGridView) }
    private val moodAdapter by lazy { MoodViewAdapter(context!!, this) }
    private val addButton by lazy { root.findViewById<FloatingActionButton>(R.id.moodsAddButton) }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
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
        gridView.setOnItemClickListener(playMoodClickListener)
    }

    private val playMoodClickListener = object : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if(view is MoodView) {
                AsyncTask.execute {
                    database.getMoodWithTasks(view.mood.id).tasks?.apply {
                        playMoodTasks(this)
                    }
                }
            }
        }
    }

    private fun playMoodTasks(moodTasks: List<MoodTask>) {
        moodTasks.forEach {
            if(it.color != null) {
                ColorSendingService.sendSingleColor(it.device!!.ipAddress, it.color!!).run()
            }
        }
    }

    private fun createMood() {
        val intent = Intent(context, MoodEditorActivity::class.java)
        startActivityForResult(intent, MOOD_EDITOR)
    }

    override fun onEditMood(mood: Mood) {
        // TODO edit mood
    }

    override fun onDeleteMood(mood: Mood) {
        // TODO delete mood
    }

    companion object {
        private const val MOOD_EDITOR = 1
    }
}