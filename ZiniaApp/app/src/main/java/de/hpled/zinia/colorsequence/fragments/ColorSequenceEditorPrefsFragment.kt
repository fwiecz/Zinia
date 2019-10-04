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
    val speed = MutableLiveData<Float>(SPEED_DEFAULT)
    val keep = MutableLiveData<Int>(KEEP_DEFAULT)

    companion object {
        const val SPEED_DEFAULT = 0.0002f
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
        viewmodel.keep.observe(this, Observer {
            keepLabel.text = getString(R.string.time_in_seconds, it / 1000, (it % 1000) / 100)
        })
        viewmodel.name.value?.apply { nameEdit.setText(this) }

        // TODO correct reverse interpolation
        viewmodel.keep.value?.apply { keepSeekbar.progress =
            (this / deInterp.getInterpolation(
                deInterp.getInterpolation(this.toFloat() / keepSeekbar.max))).toInt()
        }
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
                viewmodel.keep.value = (progress * interp.getInterpolation(
                    progress.toFloat() / keepSeekbar.max)).toInt()
                println(progress)
            }
        })
        speedSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewmodel.speed.value = interp.getInterpolation(
                    progress / resources.getInteger(R.integer.colorSeqSpeedMax).toFloat()) / 20
                println(viewmodel.speed.value ?: 0)
            }
        })
    }

    fun getName() = viewmodel.name.value ?: ""

    fun getSpeed() = viewmodel.speed.value ?: 0f

    fun getKeep() = viewmodel.keep.value ?: 0

    companion object {
        private val interp = AccelerateInterpolator()
        private val deInterp = DecelerateInterpolator()
    }
}
