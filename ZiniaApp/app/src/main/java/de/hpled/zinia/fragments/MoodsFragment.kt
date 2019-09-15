package de.hpled.zinia.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hpled.zinia.MoodEditorActivity
import de.hpled.zinia.R
import de.hpled.zinia.entities.Mood
import de.hpled.zinia.views.MoodViewAdapter
import de.hpled.zinia.views.OnMoodEditListener

class MoodsFragment : Fragment(), OnMoodEditListener {

    private lateinit var root : FrameLayout
    private val gridView by lazy { root.findViewById<GridView>(R.id.moodsGridView) }
    private val moodAdapter by lazy { MoodViewAdapter(context!!, this) }
    private val addButton by lazy { root.findViewById<FloatingActionButton>(R.id.moodsAddButton) }

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
        moodAdapter.moodList = listOf(Mood(0, "Test Mood", false, longArrayOf()))
        addButton.setOnClickListener { createMood() }
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