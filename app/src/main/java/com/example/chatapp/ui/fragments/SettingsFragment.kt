package com.example.chatapp.ui.fragments
import ContactsApi
import SettingsViewModel
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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
import kotlin.getValue

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private val authApi: AuthApi by lazy {
        RetrofitClient.create(requireContext(), AuthApi::class.java)
    }

    private val contactsApi: ContactsApi by lazy {
        RetrofitClient.create(requireContext(), ContactsApi::class.java)
    }

    private val viewModel by viewModels<SettingsViewModel> {
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        SettingsViewModel.getViewModelFactory(contactsApi, sharedPrefs)
    }

    private val pickImage = registerForActivityResult(PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateAvatar(uri, requireContext())
        } else {
            Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Загружаем аватар при открытии
        viewModel.loadUserAvatar()

        // Обновляем UI при изменении аватара
        viewModel.avatarUrl.observe(viewLifecycleOwner) { avatarUrl ->
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_person)
                .circleCrop()
                .into(binding.avatarImageView)
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
            pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            logout()
        }

        RetrofitClient.create(requireContext(), AuthApi::class.java)
            .getUser()
            .enqueue(
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

                    override fun onFailure(p0: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Ошибка загрузки: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("SettingsFragment", "getUser error", t)
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

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(error: Throwable?) {
        val message = error?.message ?: "Неизвестная ошибка"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

}