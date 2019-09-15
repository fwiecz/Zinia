package de.hpled.zinia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import de.hpled.zinia.views.MoodTaskViewAdapter

class MoodEditorActivity : AppCompatActivity() {

    private val gridView by lazy { findViewById<GridView>(R.id.moodEditorGridView) }
    private val moodTaskAdapter by lazy { MoodTaskViewAdapter(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        gridView.adapter = moodTaskAdapter
        moodTaskAdapter.moodTaskList = listOf(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mood_editor_menu, menu)
        return menu != null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.mood_editor_done -> { finish() }
            android.R.id.home -> finish()
        }
        return true
    }
}
