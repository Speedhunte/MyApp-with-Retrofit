package com.example.myapp.network

import com.example.model.Product
import com.example.myapp.data.User
import com.example.myapp.state.LoginScreenState
import com.example.myapp.data.model.ProductsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MainApi {
    @POST("auth/login")
    suspend fun login(
        @Body state: LoginScreenState
    ):User

    @GET("auth/products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): Product

    @GET("auth/products")
    suspend fun getProducts(
    ): ProductsResponse
}