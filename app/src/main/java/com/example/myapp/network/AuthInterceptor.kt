package com.example.myapp.network

import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.util.Log
import com.example.myapp.data.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.runBlocking

class AuthInterceptor(
    private val tokenProvider:suspend () -> String,
    private val tokenRepository: TokenRepository
) : Interceptor {

//    private val token: StateFlow<String> = tokenRepository.tokenFlow.stateIn(
//        CoroutineScope(Dispatchers.IO),
//        SharingStarted.Eagerly,
//        initialValue = ""
//    )
     private val token: String by lazy {
         runBlocking {
             tokenProvider()
            }
         }
        override  fun intercept(chain: Interceptor.Chain): Response {

            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            if (originalRequest.url.encodedPath == "/auth/login") {
                return chain.proceed(newRequest)
            }


            val authenticatedRequest = newRequest.newBuilder()
                .addHeader("Authorization", token)
                .build()

            return chain.proceed(authenticatedRequest)
        }
}
