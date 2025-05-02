package com.example.myapp.network

import com.example.model.Product
import com.example.myapp.data.TokenRepository
import com.example.myapp.data.User
import com.example.myapp.data.model.ProductsResponse
import com.example.myapp.state.LoginScreenState

interface AppRepository {
    suspend fun getAllProducts():ProductsResponse
    suspend fun login(state: LoginScreenState): User
    suspend fun getProductById( id : Int): Product
}

class MyAppRepository(private val api: MainApi, private val tokenRepository: TokenRepository):
    AppRepository
{
    override suspend fun getAllProducts()=api.getProducts()

    override suspend fun login(state: LoginScreenState): User {

        return api.login(state = state).also {
            tokenRepository.saveToken(it.accessToken) }
    }

    override suspend fun getProductById( id: Int): Product {
        return api.getProductById( productId = id)
    }
}