import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.ui.contacts.UserResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SettingsViewModel(
    private val contactsApi: ContactsApi,
    private var sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        fun getViewModelFactory(
            contactsApi: ContactsApi,
            sharedPreferences: SharedPreferences
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingsViewModel(
                        contactsApi = contactsApi,
                        sharedPreferences = sharedPreferences
                    )
                }
            }}

    private val _avatar = MutableLiveData<String?>()
    val avatarUrl: LiveData<String?> = _avatar

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _userData = MutableLiveData<UserResponse>()
    val userData: LiveData<UserResponse> get() = _userData

    private val _uploadState = MutableLiveData<Resource<Unit>>()
    val uploadState: LiveData<Resource<Unit>> = _uploadState

    fun loadUser() {
        viewModelScope.launch {
            try {
                _userData.value = contactsApi.getUser()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки данных"
            }
        }
    }

    sealed class Resource<T>(
        val data: T? = null,
        val error: Throwable? = null
    ) {
        class Loading<T>: Resource<T>()
        class Success<T>(data: T): Resource<T>(data = data)
        class Error<T>(error: Throwable): Resource<T>(error = error)
    }

    fun saveAvatarUrl(url: String) {
        sharedPreferences.edit().putString("avatar_url", url).apply()
    }

    fun loadAvatarUrl(): String? {
        return sharedPreferences.getString("avatar_url", null)
    }

    init {
        loadPersistedAvatar()
    }

    private fun loadPersistedAvatar() {
        viewModelScope.launch {
            _avatar.value = loadAvatarUrl() // Загружаем сохраненный URL
        }
    }

    fun updateAvatar(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uploadState.value = Resource.Loading()

            try {
                // 1. Проверка MIME-типа
                val type = context.contentResolver.getType(uri)
                    ?: throw IllegalArgumentException("Неизвестный тип файла")

                if (!type.startsWith("image/")) {
                    throw IllegalArgumentException("Выберите изображение")
                }

                // 2. Подготовка файла
                val extension = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(type)
                    ?: throw IllegalArgumentException("Неподдерживаемый формат")

                val requestBody = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes().toRequestBody(type.toMediaTypeOrNull())
                } ?: throw IOException("Не удалось прочитать файл")

                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    "${uri.lastPathSegment ?: "avatar"}.$extension",
                    requestBody
                )

                // 3. Вызов API
                val response = contactsApi.updateAvatar(filePart)

                if (response.isSuccessful) {
                    val avatarUrl = response.body() // Получаем строку с URL
                    if (!avatarUrl.isNullOrEmpty()) {
                        _avatar.value = avatarUrl
                        saveAvatarUrl(avatarUrl) // Сохраняем новый URL
                        _uploadState.value = Resource.Success(Unit)
                    } else {
                        throw IOException("Пустой ответ от сервера")
                    }
                } else {
                    when (response.code()) {
                        413 -> throw IOException("Файл слишком большой (макс. 12MB)")
                        415 -> throw IOException("Неподдерживаемый формат изображения")
                        else -> throw IOException("Ошибка сервера: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uploadState.value = Resource.Error(e)
                _errorMessage.value = when (e) {
                    is IOException -> e.message ?: "Ошибка загрузки"
                    else -> "Ошибка: ${e.localizedMessage}"
                }
            }
        }
    }

}