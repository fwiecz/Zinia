package de.hpled.zinia.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.hpled.zinia.R

/**
 * A simple [Fragment] subclass.
 */
class ColorSequenceEditorPrefsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_color_sequence_editor_prefs, container, false)
    }


}
