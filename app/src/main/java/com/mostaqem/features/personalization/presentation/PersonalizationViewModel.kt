package com.mostaqem.features.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.reciters.data.reciter.Reciter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PersonalizationViewModel @Inject constructor(
    private val repository: PersonalizationRepository
) : ViewModel() {

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

