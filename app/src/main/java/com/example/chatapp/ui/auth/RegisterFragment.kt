package com.example.chatapp.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.data.api.AuthApi
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.data.api.TokenManager
import com.example.chatapp.data.models.RegisterRequest
import com.example.chatapp.data.models.RegisterResponse
import com.example.chatapp.data.models.errors.AuthErrorBody422
import com.example.chatapp.databinding.FragmentRegisterBinding
import com.example.chatapp.ui.contacts.view_models.RegisterState
import com.example.chatapp.ui.contacts.view_models.RegisterViewModel
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

    private val authApi: AuthApi by lazy {
        RetrofitClient.create(requireContext(), AuthApi::class.java)
    }

    private val viewModel by viewModels<RegisterViewModel> {
        RegisterViewModel.getViewModelFactory(authApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterState.Loading -> showLoading()
                is RegisterState.Success -> {
                    hideLoading()
                    TokenManager.saveToken(requireContext(), state.token)
                    findNavController().navigate(R.id.action_authFragment_to_chatsFragment)
                }
                is RegisterState.Error -> {
                    hideLoading()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is RegisterState.ValidationError -> {
                    hideLoading()
                    handleValidationErrors(state.errors)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val nickname = binding.nickname.text.toString()

            if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registerUser(username, password, nickname)
        }
    }

    private fun handleValidationErrors(errors: AuthErrorBody422) {
        with(binding) {
            loginError.isVisible = false
            passwordError.isVisible = false
            nameError.isVisible = false

            errors.errors.Login?.firstOrNull()?.let {
                loginError.text = it
                loginError.isVisible = true
            }

            errors.errors.Password?.firstOrNull()?.let {
                passwordError.text = it
                passwordError.isVisible = true
            }

            errors.errors.Name?.firstOrNull()?.let {
                nameError.text = it
                nameError.isVisible = true
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}