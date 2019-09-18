package de.hpled.zinia

import android.app.Activity
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.Mood
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.fragments.DevicePickDialogFragment
import de.hpled.zinia.fragments.OnDevicePickListener
import de.hpled.zinia.fragments.OnPickMoodTaskListener
import de.hpled.zinia.fragments.PickMoodTaskDialogFragment
import de.hpled.zinia.viewmodels.MoodEditorViewModel
import de.hpled.zinia.views.MoodTaskView
import de.hpled.zinia.views.MoodTaskViewAdapter

class MoodEditorActivity : AppCompatActivity(), OnDevicePickListener, OnPickMoodTaskListener {
    private val handler = Handler()
    private val name by lazy { findViewById<EditText>(R.id.moodEditorName) }
    private val gridView by lazy { findViewById<GridView>(R.id.moodEditorGridView) }
    private val moodTaskAdapter by lazy { MoodTaskViewAdapter(applicationContext) }
    private val turnOffDevices by lazy { findViewById<Switch>(R.id.moodEditorSwitch) }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(MoodEditorViewModel::class.java)
    }
    private lateinit var devices: List<Device>
    private var allDevicesInUse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        gridView.adapter = moodTaskAdapter
        gridView.setOnItemClickListener(onItemClickListener)
        AsyncTask.execute {
            devices = database.findAllDevices()
            handler.post {
                viewmodel.moodTasks.observe(this, Observer {
                    moodTaskAdapter.moodTaskList = it + listOf(null) // add the button at the end
                    allDevicesInUse = it.mapNotNull { it.device }.toSet() == devices.toSet()
                    handler.post {
                        (gridView.children.last() as MoodTaskView).active = !allDevicesInUse
                    }
                })
            }
        }
    }

    private val onItemClickListener = object : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // If the add button was tapped
            if (view as? MoodTaskView != null && view.moodTask == null && !allDevicesInUse) {
                val alreadyUsedDevices = moodTaskAdapter.moodTaskList.mapNotNull { it?.device }
                val dialog = DevicePickDialogFragment(devices - alreadyUsedDevices, this@MoodEditorActivity)
                dialog.show(supportFragmentManager, null)
            }
        }
    }

    private fun moodCanBeSaved(): Boolean {
        return name.text.isNotEmpty() && viewmodel.moodTasks.value?.isNotEmpty()!!
    }

    private fun pickMoodTask(moodTask: MoodTask) = PickMoodTaskDialogFragment().apply {
        task = moodTask.copy().also { it.device = moodTask.device }
        listener += this@MoodEditorActivity
        show(supportFragmentManager, null)
    }

    override fun onPickMoodTask(task: MoodTask) {
        val newlist = viewmodel.moodTasks.value?.let {
            val old = it.find { it.deviceId == task.deviceId }
            val oldIndex = it.indexOf(old)
            (it - old).toMutableList().apply { add(oldIndex, task) }
        }
        viewmodel.moodTasks.value = newlist?.mapNotNull { it }
    }

    override fun onDevicePicked(device: Device) {
        val moodTask = MoodTask(0, device.id, Color.WHITE).apply { this.device = device }
        viewmodel.moodTasks.value = (viewmodel.moodTasks.value ?: listOf()) + moodTask
        pickMoodTask(moodTask)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mood_editor_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mood_editor_done -> saveAndExit()
            android.R.id.home -> finish()
        }
        return true
    }

    private fun saveAndExit() {
        val moodTasks = viewmodel.moodTasks.value
        if (moodCanBeSaved() && moodTasks != null) {
            AsyncTask.execute {
                val ids = database.moodTaskDao.insertAll( *moodTasks.toTypedArray() ).toLongArray()
                val mood = Mood(0, name.text.toString().trim(), turnOffDevices.isChecked, ids)
                database.moodDao.insert(mood)
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.mood_cannot_be_saved),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
