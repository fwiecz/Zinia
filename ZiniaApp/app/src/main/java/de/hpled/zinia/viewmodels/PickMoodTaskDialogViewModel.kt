package de.hpled.zinia.viewmodels

import androidx.lifecycle.ViewModel
import de.hpled.zinia.services.ColorSendingService

class PickMoodTaskDialogViewModel : ViewModel() {
    val colorSendingService = ColorSendingService(colorSendingFrequency)

    companion object{
        private const val colorSendingFrequency = 500L
    }
}