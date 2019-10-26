package de.hpled.zinia.moods

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.alpha
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.colorpick.fragments.OnXcolorChangeListener
import de.hpled.zinia.dto.ColorDTO
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.moods.adapter.PickMoodTaskPagerAdapter
import de.hpled.zinia.services.BrightnessSendingService
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.colorpick.views.OnBrightnessWhiteChangedListener
import de.hpled.zinia.colorpick.views.OnColorChangedListener
import de.hpled.zinia.shows.fragments.OnShowPickListener
import de.hpled.zinia.shows.interfaces.Show
import de.hpled.zinia.xcolor.Xcolor
import de.hpled.zinia.xcolor.XcolorSendingService

class PickMoodTaskActivity : AppCompatActivity(),
    OnXcolorChangeListener,
    OnShowPickListener {
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
            XcolorSendingService.sendSingleXcolor(device.ipAddress, moodTask.color).run()
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
            }
            onBrightnessWarmthChangedListener.apply {
                clear()
                add(brightnessSendingService)
            }
            onXcolorChangeListener.apply {
                clear()
                add(this@PickMoodTaskActivity)
            }
            handler.post { // Wait for fragment to be initialized
                warmthIsEnabled = moodTask.device?.isRGBW == true
                setThumbToColor(moodTask.color.toRgb())
                setBrightness(moodTask.color.brightness)
                setWarmth(moodTask.color.w)
            }
        }
        pagerAdapter.showPickFragment.apply {
            onShowPickListener.clear()
            onShowPickListener.add(this@PickMoodTaskActivity)
            database.getShowsLiveData(this@PickMoodTaskActivity)
                .observe(this@PickMoodTaskActivity, Observer {
                    setShows(it)
                })
        }
    }

    private fun onClickDone() {
        val intent = Intent()
        intent.putExtra(INTENT_MOODTASK, moodTask.apply { device = null })
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onXcolorChange(xcolor: Xcolor, type: OnXcolorChangeListener.Type, final: Boolean) {
        moodTask.color = xcolor
    }

    override fun onShowPick(show: Show) {
        // TODO integrate shows in moods
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
