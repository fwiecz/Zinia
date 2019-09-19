package de.hpled.zinia

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.views.OnColorChangedListener

class PickMoodTaskActivity : AppCompatActivity(), OnColorChangedListener {
    private val handler = Handler()
    private val databse by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val pagerAdapter by lazy {
        PickMoodTaskPagerAdapter(this, supportFragmentManager)
    }
    private val pager by lazy {
        findViewById<ViewPager>(R.id.pickMoodTaskViewPager)
    }
    private val tabs by lazy {
        findViewById<TabLayout>(R.id.pickMoodTaskTabs)
    }
    private val colorSendingService = ColorSendingService(sendingFrequency)
    private lateinit var moodTask: MoodTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_mood_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val task = intent?.getSerializableExtra(INTENT_MOODTASK) as MoodTask
        clearOldFragments()
        AsyncTask.execute {
            val device = databse.deviceDao.findById(task.deviceId)
            moodTask = task.apply { this.device = device }
            handler.post {
                supportActionBar?.setTitle(getString(R.string.pick_moodtask_dialog_title, device.name))
                onInitAsync()
            }
        }
    }

    private fun onInitAsync() {
        pager.adapter = pagerAdapter
        tabs.setupWithViewPager(pager)
        colorSendingService.targetIP = moodTask.device?.ipAddress ?: ""
        pagerAdapter.colorPickerFragment.onColorChangedListener.apply {
            clear()
            add(colorSendingService)
            add(this@PickMoodTaskActivity)
        }
    }

    private fun onClickDone() {
        val intent = Intent()
        intent.putExtra(INTENT_MOODTASK, moodTask.apply { device = null })
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onColorChanged(color: Int, final: Boolean) {
        if(final) {
            moodTask.color = color
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mood_task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.mood_task_done -> onClickDone()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        colorSendingService.stopExecutor()
    }

    private fun clearOldFragments() {
        val f = supportFragmentManager.fragments
        val t = supportFragmentManager.beginTransaction()
        f.forEach { t.remove(it) }
        t.commitNow()
    }

    companion object {
        const val INTENT_MOODTASK = "INTENT_MOODTASK"
        const val sendingFrequency = 500L
    }
}
