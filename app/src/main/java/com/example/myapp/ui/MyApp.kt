package com.example.myapp.ui
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapp.R
import com.example.myapp.state.SetLoginScreenState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapp.AppViewModelProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Route{
    @Serializable
    @SerialName("Search")
    data object Search : Route()

    @Serializable
    @SerialName("Login")
    data object Login : Route()

    @Serializable
    @SerialName("Details")
    data class Details(val productId: Int) : Route()
}

fun parseRoute(routeString: String?): Route? {
    return when {
        routeString==null -> null
        routeString == "Search" -> Route.Search
        routeString == "Login" -> Route.Login
        routeString.startsWith("Details/") -> {
            val productId = routeString.removePrefix("Details/").toIntOrNull()
            productId?.let { Route.Details(it) }
        }
        else -> null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateBack: ()->Unit
){
    CenterAlignedTopAppBar(
        title = { Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall
        ) },
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick =navigateBack){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

    )
}

@Composable
fun MyApp(
    navController: NavHostController = rememberNavController()
){
    val titleDefault = stringResource(R.string.title)
    val backStackEntry by navController.currentBackStackEntryAsState()

    var topBarTitle by remember {mutableStateOf(titleDefault)}

    LaunchedEffect(backStackEntry) {
        val route = parseRoute(backStackEntry?.destination?.route)
        topBarTitle = when (route) {
            is Route.Login -> "Login"
            is Route.Search -> "Shop"
            else -> topBarTitle
        }
    }

    val onBackHandler ={
        navController.popBackStack()
    }

    Scaffold(
        topBar ={
            MyAppBar(
                title= topBarTitle,
                canNavigateBack =navController.previousBackStackEntry!=null,
                navigateBack ={ onBackHandler() }
            )
        }
    ) {innerPadding->
        Surface (
            modifier = Modifier.padding(innerPadding)
        ){
            NavHost(
                navController = navController,
                startDestination = Route.Login,
            ) {
                composable<Route.Login> {
                    val loginViewModel:LoginViewModel= viewModel(factory = AppViewModelProvider.Factory)

                    LoginScreen(
                        isPasswordVisible = loginViewModel.isPasswordVisible,
                        setPasswordVisibility = loginViewModel::setPasswordVisibility,
                        onEvent = loginViewModel::onEvent,
                        onSignInButtonClicked = loginViewModel::login,
                        onNextPage = {
                            navController.navigate(Route.Search)
                         },
                        viewModel= loginViewModel
                    )
                }
                composable<Route.Search> {
                    SearchPage(
                        onBack = {onBackHandler()},
                        onProductClick = { id, brand ->
                            navController.navigate(Route.Details(id))
                            topBarTitle = brand
                        }
                    )
                }
                composable<Route.Details> {
                    val detailsViewModel: DetailsViewModel =
                        viewModel(factory = AppViewModelProvider.Factory)

                    when (val state = detailsViewModel.productRequestState) {
                        is SingleProductRequestState.Error -> ErrorScreen()
                        is SingleProductRequestState.Loading -> LoadingScreen()
                        is SingleProductRequestState.Success ->
                            ProductDetailsScreen(
                                state.product
                            )
                    }
                }

            }
        }

    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    isPasswordVisible: Boolean,
    setPasswordVisibility: (Boolean)->Unit,
    onEvent: (SetLoginScreenState)->Unit,
    onSignInButtonClicked:()->Unit,
    onNextPage:()->Unit
)
{
    val screenState by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
            .wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenState.username,
            onValueChange = { onEvent(SetLoginScreenState.isEmailUpdated(it)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenState.password,
            onValueChange = {
                onEvent(SetLoginScreenState.isPasswordUpdated(it))
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                val icon = if (isPasswordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                IconButton(
                    onClick = { setPasswordVisibility(!isPasswordVisible) }
                )
                {
                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            }
        )
        Button(
            onClick = {
                onSignInButtonClicked()
                onNextPage()
            }
        ) {
            Text(
                text = "Sign in"
            )
        }

    }
}

@Composable
fun LoadingScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    )
    {
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(R.drawable.loading),
            contentDescription = null
        )
    }
}
@Composable
fun ErrorScreen(){
    Text(
        text = "ошибка",
        style = MaterialTheme.typography.displayLarge
    )
}