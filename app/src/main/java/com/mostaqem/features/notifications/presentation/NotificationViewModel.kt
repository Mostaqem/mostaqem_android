package com.mostaqem.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.settings.data.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val personalizationRepository: PersonalizationRepository,

    ) : ViewModel() {

    fun toggle(value: Boolean) {
        viewModelScope.launch {
            personalizationRepository.changeFridayNotification(value)
        }

    }
}