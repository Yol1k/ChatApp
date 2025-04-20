package com.example.chatapp.ui.contacts.view_models

import SettingsViewModel
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.data.api.AuthApi
import com.example.chatapp.data.models.RegisterRequest
import com.example.chatapp.data.models.errors.AuthErrorBody422
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authApi: AuthApi
): ViewModel() {

    companion object{
        fun getViewModelFactory(
            authApi: AuthApi,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RegisterViewModel(
                        authApi = authApi
                    )
                }
            }}

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun registerUser(username: String, password: String, nickname: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val response = authApi.register(RegisterRequest(username, password, nickname))

                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        _registerState.value = RegisterState.Success(token)
                    } ?: run {
                        _registerState.value = RegisterState.Error("Токен не получен")
                    }
                } else {
                    when (response.code()) {
                        422 -> {
                            val errorBody = response.errorBody()?.string()
                            val gson = Gson()
                            val type = object : TypeToken<AuthErrorBody422>() {}.type
                            val errorResponse = gson.fromJson<AuthErrorBody422>(errorBody, type)
                            _registerState.value = RegisterState.ValidationError(errorResponse)
                        }
                        400 -> {
                            _registerState.value = RegisterState.Error(
                                response.errorBody()?.string() ?: "Неизвестная ошибка"
                            )
                        }
                        else -> {
                            _registerState.value = RegisterState.Error(
                                "Ошибка сервера: ${response.code()}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(
                    "Ошибка сети: ${e.message ?: "Неизвестная ошибка"}"
                )
            }
        }
    }
}

sealed class RegisterState {
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
    data class ValidationError(val errors: AuthErrorBody422) : RegisterState()
}