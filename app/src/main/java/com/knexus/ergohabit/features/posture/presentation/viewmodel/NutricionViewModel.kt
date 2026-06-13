package com.knexus.ergohabit.features.posture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NutricionUiState())
    val uiState: StateFlow<NutricionUiState> = _uiState.asStateFlow()
}