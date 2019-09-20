package de.hpled.zinia

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
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
import de.hpled.zinia.viewmodels.MoodEditorViewModel
import de.hpled.zinia.views.MoodTaskView
import de.hpled.zinia.views.MoodTaskViewAdapter

class MoodEditorActivity : AppCompatActivity(), OnDevicePickListener {
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
    private var moodId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(INTENT_MOOD_ID)) {
            moodId = intent.getLongExtra(INTENT_MOOD_ID, 0)
            AsyncTask.execute {
                val mood = database.getMoodWithTasks(moodId)
                handler.post {
                    viewmodel.moodTasks.value = mood.tasks
                    name.setText(mood.name)
                    turnOffDevices.isChecked = mood.turnOffUnusedDevices
                }
            }
        }
        viewmodel.moodTasks
    }

    override fun onStart() {
        super.onStart()
        gridView.adapter = moodTaskAdapter
        gridView.setOnItemClickListener(onItemClickListener)
        gridView.setOnItemLongClickListener(onLongClickListener)
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
                val dialog =
                    DevicePickDialogFragment(devices - alreadyUsedDevices, this@MoodEditorActivity)
                dialog.show(supportFragmentManager, null)
            } else if (view as? MoodTaskView != null && view.moodTask != null) {
                view.moodTask?.apply { pickMoodTask(this) }
            }
        }
    }

    private val onLongClickListener = object : AdapterView.OnItemLongClickListener {
        override fun onItemLongClick(p: AdapterView<*>?, v: View?, pos: Int, id: Long): Boolean {
            if (v != null && v is MoodTaskView) {
                v.moodTask?.apply { deleteMoodTask(this) }
            }
            return true
        }
    }

    private fun moodCanBeSaved(): Boolean {
        return name.text.isNotEmpty() && viewmodel.moodTasks.value?.isNotEmpty()!!
    }

    private fun pickMoodTask(moodTask: MoodTask) {
        val intent = Intent(this, PickMoodTaskActivity::class.java)
        intent.putExtra(PickMoodTaskActivity.INTENT_MOODTASK, moodTask)
        startActivityForResult(intent, REQUEST_PICK_MOODTASK)
    }

    private fun onMoodTaskHasChanged(task: MoodTask) {
        val newlist = viewmodel.moodTasks.value?.let {
            val old = it.find { it.deviceId == task.deviceId }
            val oldIndex = it.indexOf(old)
            (it - old).toMutableList().apply { add(oldIndex, task) }
        }
        viewmodel.moodTasks.value = newlist?.mapNotNull { it }
    }

    override fun onDevicePicked(device: Device) {
        val moodTask = MoodTask(
            0,
            device.id,
            Color.WHITE,
            resources.getInteger(R.integer.maxBrightness)
        ).apply { this.device = device }
        viewmodel.moodTasks.value = (viewmodel.moodTasks.value ?: listOf()) + moodTask
        pickMoodTask(moodTask)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_MOODTASK -> if (resultCode == Activity.RESULT_OK && data != null) {
                val task =
                    data.getSerializableExtra(PickMoodTaskActivity.INTENT_MOODTASK) as MoodTask
                AsyncTask.execute {
                    task.device = database.deviceDao.findById(task.deviceId)
                    handler.post { onMoodTaskHasChanged(task) }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun deleteMoodTask(moodTask: MoodTask) {
        AlertDialog.Builder(this, R.style.DefaultAlertDialogStyle).apply {
            setTitle(getString(R.string.remove_device_title))
            setMessage(getString(R.string.remove_device_message, moodTask.device?.name))
            setNegativeButton(getString(R.string.cancel_label)) { dialog, which -> }
            setPositiveButton(getString(R.string.remove_label)) { dialog, which ->
                viewmodel.moodTasks.value = viewmodel.moodTasks.value!! - moodTask
            }
            create()
            show()
        }
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
                // delete unused MoodTasks
                if (database.moodDao.findAll().map { it.id }.contains(moodId)) {
                    val oldMoodTasks = database.getMoodWithTasks(moodId).tasks ?: listOf()
                    val newTasks = moodTasks.map { it.id }
                    val delTasks = oldMoodTasks.filter { it.id !in newTasks }
                    database.moodTaskDao.deleteAll(*delTasks.toTypedArray())
                }

                val ids = database.moodTaskDao.insertAll(*moodTasks.toTypedArray()).toLongArray()
                val mood = Mood(moodId, name.text.toString().trim(), turnOffDevices.isChecked, ids)
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

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    companion object {
        const val INTENT_MOOD_ID = "INTENT_MOOD_ID"
        const val REQUEST_PICK_MOODTASK = 1
    }
}
