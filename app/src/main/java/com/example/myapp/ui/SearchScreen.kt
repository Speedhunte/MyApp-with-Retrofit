package com.example.myapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Product
import com.example.myapp.AppViewModelProvider
import com.example.myapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    onBack:()->Unit,
    onProductClick:(Int, String)->Unit,
    viewModel: SearchViewModel= viewModel(factory = AppViewModelProvider.Factory),
){
    BackHandler {
        onBack()
    }
    val searchState by viewModel.searchState.collectAsState()
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        SearchBar(
            windowInsets = WindowInsets(0),
            colors = SearchBarDefaults.colors(containerColor = Color.Cyan ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchState.query,
                    onQueryChange = {
                        viewModel.onQueryChanged(it)
                    }
                    ,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    expanded = searchState.isExpanded,
                    onExpandedChange = {viewModel.toggleExpanded()},
                    onSearch = {
                        viewModel.toggleExpanded()
                        viewModel.onSearchSubmit()
                    },

                    )
            },
            expanded = searchState.isExpanded,
            onExpandedChange = {viewModel.toggleExpanded()},
            modifier = Modifier.fillMaxWidth()
        ){
            val historyItems = if (searchState.query.isEmpty()) {
                viewModel.searchHistory
            } else {
                viewModel.filteredItems.map { it.title }
            }
            LazyColumn {
                items(historyItems) { item ->
                    ListItem(
                        headlineContent = { Text(
                            text = item
                        ) },
                        modifier = Modifier
                            .clickable {
                                viewModel.updateQuery(item)
                                viewModel.onSearchSubmit()
                                viewModel.toggleExpanded()
                            }
                            .background(Color.White)

                    )
                }
            }
        }
        when(val state=viewModel.requestState){
            is RequestState.Loading-> LoadingScreen()
            is RequestState.Error-> ErrorScreen()
            is RequestState.Success-> {
                val listToShow= if(searchState.query.isEmpty()){
                    state.products
                } else {
                    viewModel.filteredItems
                }
                ListOfProducts(
                    onProductClick,
                    listToShow
                )
            }
        }

    }
}


@Composable
fun ListOfProducts(
    onProductClick: (Int, String) -> Unit,
    list: List<Product>
){

    LazyVerticalGrid(
        columns =
            GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp)

    ) {
        items(list){
            ProductCard(it, onProductClick)
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Int, String) -> Unit
){
    Card(
        modifier = Modifier
            .clickable { onProductClick(product.id, product.brand?:"") }
            .height(300.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                modifier = Modifier.padding(start = 5.dp, top = 5.dp),
                text = product.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(product.thumbnail)
                    .crossfade(true)
                    .build()
                ,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(R.drawable.loading_img)
            )
        }
    }
}
