package de.hpled.zinia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import de.hpled.zinia.entities.Device
import de.hpled.zinia.fragments.OnPreviewControllerActionListener
import de.hpled.zinia.fragments.PreviewControllerFragment
import java.lang.IllegalStateException

class ColorSequenceEditorActivity : AppCompatActivity(), OnPreviewControllerActionListener {

    private val previewController by lazy {
        supportFragmentManager.findFragmentById(R.id.colorSequencePreview)
                as PreviewControllerFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_sequence_editor)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        previewController.onPreviewControllerActionListener += this
    }

    override fun onPreviewPlay(device: Device) { }

    override fun onPreviewStop(device: Device) { }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
