package de.hpled.zinia.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.entities.MoodTask

class MoodEditorViewModel : ViewModel() {
    val moodTasks = MutableLiveData<List<MoodTask>>(listOf())
}