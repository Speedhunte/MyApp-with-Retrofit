package com.example.myapp

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapp.ui.DetailsViewModel
import com.example.myapp.ui.LoginViewModel
import com.example.myapp.ui.SearchViewModel

object AppViewModelProvider {
val Factory = viewModelFactory {

    initializer {
        LoginViewModel(
            myApplication().container.myAppRepository,
            myApplication().container.tokenRepository
        )
    }
    initializer {
        SearchViewModel(
            myApplication().container.myAppRepository,
            myApplication().container.tokenRepository
        )
    }
    initializer {
        DetailsViewModel(
            this.createSavedStateHandle(),
            myApplication().container.myAppRepository,
            myApplication().container.tokenRepository
        )
    }
}

}

fun CreationExtras.myApplication()=
    (this[APPLICATION_KEY] as MyApplication)