package com.example.chatapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.data.api.AuthApi
import com.example.chatapp.data.models.LoginRequest
import com.example.chatapp.data.models.RegisterRequest
import com.example.chatapp.data.models.errors.AuthErrorBody422
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class LoginViewModel(private val authApi: AuthApi): ViewModel() {

    companion object{
        fun getViewModelFactory(
            authApi: AuthApi,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    LoginViewModel(
                        authApi = authApi
                    )
                }
            }}

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun loginUser(login: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = authApi.login(LoginRequest(login, password))

                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        _loginState.value = LoginState.Success(token)
                    } ?: run {
                        _loginState.value = LoginState.Error("Токен не получен")
                    }
                } else {
                    when (response.code()) {
                        422 -> {
                            val errorBody = response.errorBody()?.string()
                            val gson = Gson()
                            val type = object : TypeToken<AuthErrorBody422>() {}.type
                            val errorResponse = gson.fromJson<AuthErrorBody422>(errorBody, type)
                            _loginState.value = LoginState.ValidationError(errorResponse)
                        }
                        400 -> {
                            _loginState.value = LoginState.Error(
                                response.errorBody()?.string() ?: "Неизвестная ошибка"
                            )
                        }
                        else -> {
                            _loginState.value = LoginState.Error(
                                "Ошибка сервера: ${response.code()}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    "Ошибка сети: ${e.message ?: "Неизвестная ошибка"}"
                )
            }
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
    data class ValidationError(val errors: AuthErrorBody422) : LoginState()
}