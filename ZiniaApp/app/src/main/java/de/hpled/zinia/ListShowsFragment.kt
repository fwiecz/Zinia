package de.hpled.zinia

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class ListShowsFragment : Fragment() {

    companion object {
        fun newInstance() = ListShowsFragment()
    }

    private lateinit var viewModel: ListShowsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_shows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListShowsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
