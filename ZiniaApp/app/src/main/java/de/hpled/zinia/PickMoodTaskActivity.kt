package de.hpled.zinia

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.hpled.zinia.dto.ColorDTO
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.services.BrightnessSendingService
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.views.OnBrightnessWarmthChangedListener
import de.hpled.zinia.views.OnColorChangedListener

class PickMoodTaskActivity : AppCompatActivity(), OnColorChangedListener,
    OnBrightnessWarmthChangedListener {
    private val handler = Handler()
    private val database by lazy {
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
    private val brightnessSendingService = BrightnessSendingService(sendingFrequency)
    private lateinit var moodTask: MoodTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_mood_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val task = intent?.getSerializableExtra(INTENT_MOODTASK) as MoodTask
        clearOldFragments()
        AsyncTask.execute {
            val device = database.deviceDao.findById(task.deviceId)
            moodTask = task.apply { this.device = device }
            handler.post {
                supportActionBar?.setTitle(
                    getString(
                        R.string.pick_moodtask_dialog_title,
                        device.name
                    )
                )
                onInitAsync()
            }
        }
    }

    private fun onInitAsync() {
        pager.adapter = pagerAdapter
        tabs.setupWithViewPager(pager)
        colorSendingService.targetIP = moodTask.device?.ipAddress ?: ""
        brightnessSendingService.targetIP = moodTask.device?.ipAddress ?: ""
        pagerAdapter.colorPickerFragment.apply {
            onColorChangedListener.apply {
                clear()
                add(colorSendingService)
                add(this@PickMoodTaskActivity)
            }
            onBrightnessWarmthChangedListener.apply {
                clear()
                add(brightnessSendingService)
                add(this@PickMoodTaskActivity)
            }
            handler.post { // Wait for fragment to be initialized
                setThumbToColor(ColorDTO.from(moodTask.color ?: Color.WHITE))
                setBrightness(moodTask.brightness)
            }
        }
    }

    private fun onClickDone() {
        val intent = Intent()
        intent.putExtra(INTENT_MOODTASK, moodTask.apply { device = null })
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onColorChanged(color: Int, final: Boolean) {
        if (final) {
            moodTask.color = color
        }
    }

    override fun onBrightnessChanged(value: Int, final: Boolean) {
        moodTask.brightness = value
    }

    override fun onWarmthChanged(value: Int, final: Boolean) {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mood_task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mood_task_done -> onClickDone()
            android.R.id.home -> finish()
        }
        return true
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

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val INTENT_MOODTASK = "INTENT_MOODTASK"
        const val sendingFrequency = 500L
    }
}
