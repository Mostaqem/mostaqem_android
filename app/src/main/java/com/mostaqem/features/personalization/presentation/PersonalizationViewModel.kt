package com.mostaqem.features.personalization.presentation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.dataStore
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.settings.data.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PersonalizationViewModel @Inject constructor(
    private val repository: PersonalizationRepository
) : ViewModel() {


    val userSettings: StateFlow<AppSettings> =
        repository.appSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppSettings()
        )

    fun changeShape(id: String) {
        viewModelScope.launch {
            repository.changeShape(id)
        }
    }

    fun changeRecitationID(id: Int) {
        viewModelScope.launch {
            repository.changeRecitationID(id)
        }
    }

    fun saveDefaultReciter(reciter: Reciter) {
        viewModelScope.launch {
            repository.saveDefaultReciter(reciter = reciter)
        }
    }

}

