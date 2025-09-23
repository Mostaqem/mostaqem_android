package com.mostaqem.features.language.presentation

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.features.language.domain.AppLanguages
import com.mostaqem.features.language.domain.LanguageManager
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.settings.data.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val manager: LanguageManager,
    private val personalizationRepository: PersonalizationRepository
) : ViewModel() {

    fun changeLanguage(languageCode: String) {
        val appLanguage = AppLanguages.entries.find { it.code == languageCode }
        viewModelScope.launch {
            personalizationRepository.changeLanguageDatastore(appLanguage!!)
        }
        manager.changeLanguage(languageCode)

    }
}