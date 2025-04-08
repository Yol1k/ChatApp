package com.example.chatapp.ui.fragments
import ContactsApi
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.data.api.AuthApi
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.data.models.UserResponse
import com.example.chatapp.databinding.FragmentSettingsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var authApi: AuthApi
    private lateinit var sharedPreferences: SharedPreferences

    private val contactsApi by lazy {
        RetrofitClient.create(requireContext(), view, ContactsApi::class.java)
    }
    private val viewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.getViewModelFactory(contactsApi)
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it, requireContext()) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализируем sharedPreferences в onCreate
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nogamenolife.pro/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        authApi = retrofit.create(AuthApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSavedAvatar()

        viewModel.avatarUrl.observe(viewLifecycleOwner) { newUrl ->
            newUrl?.let { url ->
                saveAvatarLocally(url)
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.avatarImageView)
            }
        }


        binding.settings.text = "Настройки"

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setDarkTheme()
            } else {
                setLightTheme()
            }
        }

        binding.changeAvatarButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Обновляем аватар при изменении LiveData
        viewModel.avatarUrl.observe(viewLifecycleOwner) { newUrl ->
            Glide.with(this)
                .load(newUrl)
                .placeholder(R.drawable.ic_person) // Заглушка
                .circleCrop() // Круглый аватар
                .into(binding.avatarImageView)
        }

        // Показываем ошибки
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.nogamenolife.pro/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            authApi = retrofit.create(AuthApi::class.java)

            sharedPreferences =
                requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

            binding.btnLogout.setOnClickListener {
                logout()
            }

            RetrofitClient.create(requireContext(), view, AuthApi::class.java).getUser().enqueue(
                object :
                    Callback<UserResponse> {
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.name.let { name ->
                                binding.username.text = name
                            }
                        }
                    }

                    override fun onFailure(p0: Call<UserResponse?>, p1: Throwable) {
                    }
                })

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        private fun setDarkTheme() {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        private fun setLightTheme() {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        private fun logout() {
            // Получаем токен из SharedPreferences
            val token = sharedPreferences.getString("auth_token", null)

            if (token != null) {
                // Вызываем API для выхода
                val call = authApi.logout()
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // Очищаем SharedPreferences
                            sharedPreferences.edit().clear().apply()

                            // Перенаправляем пользователя на экран входа
                            findNavController().navigate(R.id.authFragment)
                        } else {
                            // Обработка ошибки
                            Toast.makeText(
                                requireContext(),
                                "Ошибка при выходе",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Обработка ошибки сети
                        Toast.makeText(
                            requireContext(),
                            "Ошибка сети: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                // Если токен отсутствует, просто перенаправляем на экран входа
                findNavController().navigate(R.id.authFragment)
            }
        }

    private fun saveAvatarLocally(avatarUrl: String) {
        sharedPreferences.edit().putString("avatar_url", avatarUrl).apply()
    }

    private fun loadSavedAvatar() {
        val savedUrl = sharedPreferences.getString("avatar_url", null)
        savedUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_person)
                .circleCrop()
                .into(binding.avatarImageView)
        }
    }

    private fun clearAvatar() {
        sharedPreferences.edit().remove("avatar_url").apply()
    }

}