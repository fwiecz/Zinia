package de.hpled.zinia

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.fragments.DevicePickDialogFragment
import de.hpled.zinia.fragments.OnDevicePickListener
import de.hpled.zinia.views.MoodTaskView
import de.hpled.zinia.views.MoodTaskViewAdapter

class MoodEditorActivity : AppCompatActivity(), OnDevicePickListener {

    private val gridView by lazy { findViewById<GridView>(R.id.moodEditorGridView) }
    private val moodTaskAdapter by lazy { MoodTaskViewAdapter(applicationContext) }
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private lateinit var devices: List<Device>
    private val moodTasks = mutableListOf<MoodTask?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        gridView.adapter = moodTaskAdapter
        gridView.setOnItemClickListener(onItemClickListener)
        moodTaskAdapter.moodTaskList = listOf(null) // only the button is in list
        AsyncTask.execute { devices = database.findAllDevices() }
    }

    private val onItemClickListener = object : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // If the add button was tapped
            if(view as? MoodTaskView != null && view.moodTask == null) {
                val alreadyUsedDevices = moodTaskAdapter.moodTaskList.mapNotNull { it?.device }
                val dialog = DevicePickDialogFragment(devices - alreadyUsedDevices, this@MoodEditorActivity)
                dialog.show(supportFragmentManager, null)
            }
        }
    }

    override fun onDevicePicked(device: Device) {
        val moodTask = MoodTask(0, device.id, Color.WHITE).apply { this.device = device}
        moodTasks += moodTask
        moodTaskAdapter.moodTaskList = (moodTasks.toList() + listOf<MoodTask?>(null)) // adds the button
        println(moodTaskAdapter.moodTaskList)
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
