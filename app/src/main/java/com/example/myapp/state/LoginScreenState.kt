package com.example.myapp.state

data class LoginScreenState(
    val username: String="",
    val password: String="",
    val isPasswordVisible: Boolean = false
)


sealed class SetLoginScreenState{
    data class isEmailUpdated(val newEmail: String ): SetLoginScreenState()
    data class isPasswordUpdated(val newPassword: String): SetLoginScreenState()
}