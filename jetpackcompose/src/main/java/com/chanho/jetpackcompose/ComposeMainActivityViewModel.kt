package com.chanho.jetpackcompose

import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class ComposeMainActivityViewModel @Inject constructor(
    userDataRepository:UserRepository,
) :ViewModel(){
    val uiState :StateFlow<MainActivityUiState> = userDataRepository.u
}


sealed interface MainActivityUiState{
    object Loading:MainActivityUiState
    data class Success(val userData: UserData):MainActivityUiState
}