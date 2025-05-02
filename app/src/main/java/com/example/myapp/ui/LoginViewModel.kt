package com.example.myapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.TokenRepository
import com.example.myapp.state.SetLoginScreenState
import com.example.myapp.state.LoginScreenState
import com.example.myapp.network.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: AppRepository,
                     private val tokenRepository: TokenRepository
    ): ViewModel() {

    var isPasswordVisible by mutableStateOf(false)
        private set

    private val _state  = MutableStateFlow(LoginScreenState("emilys", "emilyspass"))
    val state:StateFlow<LoginScreenState> = _state.asStateFlow()

    fun login(){
        viewModelScope.launch {
            repository.login(state.value)
        }
    }

    fun setPasswordVisibility(newVisibility:Boolean){
        isPasswordVisible=newVisibility
    }
    fun onEvent(event: SetLoginScreenState){
        _state.value = when(event){
            is SetLoginScreenState.isEmailUpdated-> _state.value.copy(username = event.newEmail)
            is SetLoginScreenState.isPasswordUpdated ->_state.value.copy(password = event.newPassword)
        }
    }


}