package de.hpled.zinia.colorsequence.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.R

class ColorSequenceEditorPrefsViewModel : ViewModel() {
    val name = MutableLiveData<String>("")
    val speed = MutableLiveData<Int>(SPEED_DEFAULT)
    val keep = MutableLiveData<Int>(KEEP_DEFAULT)

    fun setSpeedValue(progress: Int, max: Int) {
        speed.value = (progress * interp.getInterpolation(progress.toFloat() / max) + 1).toInt()
    }

    fun setKeepValue(progress: Int, max: Int) {
        keep.value = (progress * interp.getInterpolation(progress.toFloat() / max)).toInt()
    }

    companion object {
        private val interp = AccelerateInterpolator()
        const val SPEED_DEFAULT = 10
        const val KEEP_DEFAULT = 3000
    }
}

class ColorSequenceEditorPrefsFragment : Fragment() {

    private lateinit var root: LinearLayout
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(ColorSequenceEditorPrefsViewModel::class.java)
    }
    private val nameEdit by lazy {
        root.findViewById<EditText>(R.id.colorSeqEditorName)
    }
    private val speedSeekbar by lazy {
        root.findViewById<SeekBar>(R.id.colorSeqEditorSpeed)
    }
    private val speedLabel by lazy {
        root.findViewById<TextView>(R.id.colorSeqEditorSpeedPercent)
    }
    private val keepSeekbar by lazy {
        root.findViewById<SeekBar>(R.id.colorSeqEditorKeep)
    }
    private val keepLabel by lazy {
        root.findViewById<TextView>(R.id.colorSeqEditorKeepTime)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_color_sequence_editor_prefs, container, false)
                as LinearLayout
        return root
    }

    override fun onStart() {
        super.onStart()

        viewmodel.speed.observe(this, Observer {
            speedLabel.text = getString(R.string.percent_one_decimal_label, it / 10, it % 10)
        })
        viewmodel.keep.observe(this, Observer {
            keepLabel.text = getString(R.string.time_in_seconds, it / 1000, (it % 1000) / 100)
        })
        viewmodel.name.value?.apply { nameEdit.setText(this) }

        initListener()
    }

    private fun initListener() {
        nameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewmodel.name.value = s?.toString() ?: ""
            }
        })
        keepSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewmodel.setKeepValue(progress, keepSeekbar.max)
            }
        })
        speedSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewmodel.setSpeedValue(progress, speedSeekbar.max)
            }
        })
    }

    fun setPrefs(name: String, speed: Int, keep: Int) {
        viewmodel.name.value = name
        nameEdit.setText(name)
        viewmodel.speed.value = speed
        viewmodel.keep.value = keep
    }

    fun getName() = viewmodel.name.value ?: ""

    fun getSpeed() = viewmodel.speed.value ?: 1

    fun getKeep() = viewmodel.keep.value ?: 0
}
