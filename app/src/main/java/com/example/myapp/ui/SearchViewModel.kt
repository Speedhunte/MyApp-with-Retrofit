package com.example.myapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Product
import com.example.myapp.data.SearchState
import com.example.myapp.data.TokenRepository
import com.example.myapp.network.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface RequestState{
    object Loading:RequestState
    object Error: RequestState
    data class Success(val products:List<Product>): RequestState
}
class SearchViewModel(private val repository: AppRepository,
     tokenRepository: TokenRepository): ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    var requestState: RequestState by mutableStateOf((RequestState.Loading))

    var searchHistory = mutableStateListOf<String>()
        private set

//    private val tokenState: StateFlow<String> = tokenRepository.tokenFlow.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5_000L),
//        ""
//    )
    var filteredItems by mutableStateOf(listOf<Product>())
        private set


    init {
//        viewModelScope.launch {
//            tokenState
//                .filter {
//                    it.isNotBlank()
//                }
//                .first()
//                .also {
//                    getProducts(it)
//                }
//
//        viewModelScope.launch {
//            val token = tokenRepository.awaitInitialToken()
//            Log.d("mytag", token)
//            getProducts(token)
//        }
        viewModelScope.launch {
            //val token = tokenRepository.getToken()
            getProducts()
        }

    }

    private suspend fun getProducts (){
        //viewModelScope.launch {
        requestState=RequestState.Loading
        requestState=try {
            //Log.d("mytag", tokenState.value)
            val response = repository.getAllProducts()
            RequestState.Success(response.products)
        }
        catch (e: IOException) {
            RequestState.Error
        }
        //}
    }

    fun onQueryChanged(newQuery: String) {
        updateQuery(newQuery)
        val currentState = requestState
        if (currentState is RequestState.Success) {
            filteredItems = currentState.products.filter {
                it.title.startsWith(newQuery, ignoreCase = true)
            }
        }
    }
    fun updateQuery(newQuery: String) {
        _searchState.update { currentState ->
            currentState.copy(query = newQuery)
        }
    }
    fun toggleExpanded() {
        _searchState.update { currentState ->
            currentState.copy(isExpanded = !currentState.isExpanded)
        }
    }

    fun onSearchSubmit() {
        val curQuery = searchState.value.query
        if (curQuery.isNotBlank() && !searchHistory.contains(curQuery)) {
            searchHistory.add(curQuery)
        }
    }

}