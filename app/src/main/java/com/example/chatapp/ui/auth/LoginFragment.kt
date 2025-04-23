package com.example.chatapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.data.api.AuthApi
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.data.api.TokenManager
import com.example.chatapp.data.models.errors.AuthErrorBody422
import com.example.chatapp.databinding.FragmentLoginBinding
import com.example.chatapp.databinding.FragmentRegisterBinding

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authApi: AuthApi by lazy {
        RetrofitClient.create(requireContext(), AuthApi::class.java)
    }

    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.getViewModelFactory(authApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.Loading -> showLoading()
                is LoginState.Success -> {
                    hideLoading()
                    TokenManager.saveToken(requireContext(), state.token)
                    findNavController().navigate(R.id.action_authFragment_to_chatsFragment)
                }
                is LoginState.Error -> {
                    hideLoading()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is LoginState.ValidationError -> {
                    hideLoading()
                    handleValidationErrors(state.errors)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.loginUser(username, password)
        }
    }

    private fun handleValidationErrors(errors: AuthErrorBody422) {
        with(binding) {
            loginError.isVisible = false
            passwordError.isVisible = false

            errors.errors.Login?.firstOrNull()?.let {
                loginError.text = it
                loginError.isVisible = true
            }

            errors.errors.Password?.firstOrNull()?.let {
                passwordError.text = it
                passwordError.isVisible = true
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