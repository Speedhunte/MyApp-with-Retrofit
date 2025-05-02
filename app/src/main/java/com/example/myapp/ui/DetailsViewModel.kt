package com.example.myapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.toRoute
import com.example.model.Product
import com.example.myapp.data.TokenRepository
import com.example.myapp.network.AppRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface SingleProductRequestState{
    object Error: SingleProductRequestState
    object Loading: SingleProductRequestState
    data class  Success(val product: Product): SingleProductRequestState
}

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val  appRepository: AppRepository,
    private val tokenRepository: TokenRepository
): ViewModel() {

    var productRequestState: SingleProductRequestState by mutableStateOf(SingleProductRequestState.Loading)

    private val route: Route.Details = savedStateHandle.toRoute()
    private val productId: Int = route.productId

    init{
        loadProductById()
    }
    private fun loadProductById() {
        viewModelScope.launch {
            productRequestState=try {
                val product = appRepository.getProductById(id = productId)
                SingleProductRequestState.Success(product)
            }catch (e: IOException) {
                SingleProductRequestState.Error
            }
            //val token = tokenRepository.getToken()
        }

    }
}