package com.example.chatapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.data.api.AuthApi
import com.example.chatapp.data.api.TokenManager
import com.example.chatapp.data.models.RegisterRequest
import com.example.chatapp.data.models.RegisterResponse
import com.example.chatapp.data.models.errors.AuthErrorBody422
import com.example.chatapp.databinding.FragmentRegisterBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RegisterFragment: Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authApi: AuthApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nogamenolife.pro/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        authApi = retrofit.create(AuthApi::class.java)

        binding.btnRegister.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val nickname = binding.nickname.text.toString()

            if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                showError("Пожалуйста, заполните все поля")
                return@setOnClickListener
            }

            registerUser(username, password, nickname)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerUser(username: String, password: String, nickname: String) {
        val call = authApi.register(RegisterRequest(username, password, nickname))
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        TokenManager.saveToken(requireContext(), token)
                        findNavController().navigate(R.id.action_authFragment_to_chatsFragment)
                    } else {
                        showError("Ошибка: токен не получен")
                    }
                } else if (response.code() == 422) {
                    val gson = Gson()
                    val type = object : TypeToken<AuthErrorBody422>() {}.type
                    val errorResponse: AuthErrorBody422? = gson.fromJson(response.errorBody()!!.charStream(), type)

                    errorResponse?.errors?.Login?.let { errors ->
                        binding.loginError.isVisible = true
                        binding.loginError.text = errors.first()
                    }

                    errorResponse?.errors?.Password?.let { errors ->
                        binding.passwordError.isVisible = true
                        binding.passwordError.text = errors.first()
                    }


                    errorResponse?.errors?.Name?.let { errors ->
                        binding.nameError.isVisible = true
                        binding.nameError.text = errors.first()
                    }

                }
                else if (response.code() == 400) {
                    binding.loginOrPasswordError.text = response.errorBody()?.charStream()?.readText()
                    binding.loginOrPasswordError.isVisible = true
                }

                else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("NetworkError", "Ошибка сервера: $errorBody")
                    showError("Ошибка сервера: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("NetworkError", "Ошибка сети", t)
                showError("Ошибка сети: ${t.message ?: "Неизвестная ошибка"}")
            }
        })
    }


    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}