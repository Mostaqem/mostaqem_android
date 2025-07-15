package com.mostaqem.features.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.features.favorites.data.FavoritedAudio
import com.mostaqem.features.favorites.domain.FavoritesRepository
import com.mostaqem.features.surahs.data.AudioData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    val favorities: StateFlow<List<FavoritedAudio>> = repository.getFavorites().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun removeFavorite(audio: FavoritedAudio){
        viewModelScope.launch {
            repository.removeFavorite(audio)
        }
    }

}