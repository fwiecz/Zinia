package de.hpled.zinia.colorsequence

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.colorsequence.adapters.ColorsequenceEditorPagerAdapter
import de.hpled.zinia.entities.Device
import de.hpled.zinia.colorsequence.fragments.ColorSequenceEditorFragment
import de.hpled.zinia.colorsequence.fragments.ColorSequenceEditorPrefsFragment
import de.hpled.zinia.colorsequence.views.OnSegmentClickListener
import de.hpled.zinia.colorsequence.views.StaticViewPager
import de.hpled.zinia.entities.ColorSequence
import de.hpled.zinia.fragments.ColorPickerFragment
import de.hpled.zinia.fragments.OnPreviewControllerActionListener
import de.hpled.zinia.fragments.PreviewControllerFragment
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.views.OnColorChangedListener
import java.util.concurrent.ScheduledThreadPoolExecutor

class ColorSequenceEditorActivityViewModel : ViewModel() {
    var editIndex: Int? = null
    var editColor: Int = Color.WHITE
    val executor = ScheduledThreadPoolExecutor(1)
    val colorSendingService = ColorSendingService(SENDING_FREQ)

    companion object {
        private const val SENDING_FREQ = 300L
    }
}

class ColorSequenceEditorActivity : AppCompatActivity(), OnPreviewControllerActionListener,
    OnSegmentClickListener, OnColorChangedListener {

    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(ColorSequenceEditorActivityViewModel::class.java)
    }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val previewController by lazy {
        supportFragmentManager.findFragmentById(R.id.colorSequencePreview)
                as PreviewControllerFragment
    }
    private val editorPrefs by lazy {
        supportFragmentManager.findFragmentById(R.id.colorSequencePrefs)
                as ColorSequenceEditorPrefsFragment
    }
    private val pager by lazy {
        findViewById<StaticViewPager>(R.id.colorSequencePager)
    }
    private val pagerAdapter by lazy {
        ColorsequenceEditorPagerAdapter(supportFragmentManager)
    }
    private val sequence by lazy {
        supportFragmentManager.fragments.find { it is ColorSequenceEditorFragment }
                as ColorSequenceEditorFragment
    }
    private val colorPicker by lazy {
        supportFragmentManager.fragments.find { it is ColorPickerFragment } as ColorPickerFragment
    }
    private val handler = Handler()
    private var targetId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_sequence_editor)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        pager.adapter = pagerAdapter

        targetId = intent.getLongExtra(INTENT_COLOR_SEQ_ID, 0L)
        if(targetId != 0L) {
            AsyncTask.execute {
                try {
                    val cs = database.colorSequenceDao.findById(targetId)
                    handler.post {
                        editorPrefs.setPrefs(cs.name, cs.transitionSpeed, cs.keepingTimeMillis)
                        // wait for initialization
                        handler.post { sequence.setColors(cs.colors) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        handler.post {
            sequence.onSegmentClickListener += this
            previewController.onPreviewControllerActionListener += this
            colorPicker.setOnDoneListener {
                pager.currentItem = 0
                viewmodel.editIndex?.apply { sequence.setColorAt(this, viewmodel.editColor) }
                sequence.nextColor = viewmodel.editColor
            }
            colorPicker.onColorChangedListener.apply {
                clear()
                add(this@ColorSequenceEditorActivity)
                add(viewmodel.colorSendingService)
            }
        }
    }

    override fun onPreviewPlay(device: Device) {
        val seq = buildColorSequence()
        viewmodel.executor.execute(seq.getSendingJob(device.ipAddress))
    }

    override fun onPreviewStop(device: Device) {}

    override fun onDeviceChanged(device: Device?) {
        viewmodel.colorSendingService.targetIP = device?.ipAddress ?: ""
    }

    private fun buildColorSequence() = ColorSequence(
        targetId,
        editorPrefs.getName(),
        editorPrefs.getSpeed(),
        editorPrefs.getKeep(),
        sequence.getColors().toIntArray()
    )

    override fun onColorChanged(color: Int, final: Boolean) {
        if(final) {
            viewmodel.editColor = color
        }
    }

    override fun onSegmentClick(index: Int, color: Int) {
        viewmodel.editIndex = index
        viewmodel.editColor = color
        colorPicker.setThumbToColor(color)
        handler.post {
            pager.currentItem = 1
        }
    }

    private fun checkSaveable() : Boolean {
        return editorPrefs.getName().isNotEmpty() && sequence.getColors().isNotEmpty()
    }

    private fun onClickSave() {
        if(checkSaveable()) {
            AsyncTask.execute {
                val seq = buildColorSequence()
                database.colorSequenceDao.insert(seq)
                handler.post { finish() }
            }
        }
        else {
            Toast.makeText( applicationContext,
                getString(R.string.color_sequence_cannot_save),
                Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.save_menu_save -> {
                onClickSave()
            }
        }
        return true
    }

    override fun finish() {
        database.close()
        super.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val INTENT_COLOR_SEQ_ID = "INTENT_COLOR_SEQ_ID"
    }
}
