package com.example.myapp.network

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.myapp.data.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer{
    val myAppRepository: AppRepository
    val tokenRepository: TokenRepository
}
class MyAppContainer(private val dataStore: DataStore<Preferences>): AppContainer{
    private val baseUrl="https://dummyjson.com/"

    override val tokenRepository by lazy {
        TokenRepository(dataStore)
    }

//    init{
//        runBlocking {
//            tokenRepository.clearToken()
//                //Log.d("mytag", "after clearing")
//        }
//    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(
            tokenRepository::getToken,
            tokenRepository
            )
        )
        .build()

    private val api:MainApi by lazy {  Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(MainApi::class.java)
    }
    override val myAppRepository by lazy {
        MyAppRepository(api, tokenRepository)
    }

}