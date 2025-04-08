import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel

class SettingsViewModel(val ContactsApi: ContactsApi) : ViewModel() {

    companion object {
        fun getViewModelFactory(ContactsApi: ContactsApi): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingsViewModel(
                        ContactsApi = ContactsApi
                    )
                }
            }}

    private val _avatar = MutableLiveData<String?>()
    val avatarUrl: LiveData<String?> = _avatar

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun uploadAvatar(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                //Получаем MIME-тип и расширение файла
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

                //Создаем RequestBody из Uri
                val requestBody = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())
                } ?: return@launch

                //Формируем MultipartBody.Part
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    "avatar_${System.currentTimeMillis()}.$extension",
                    requestBody
                )

                //Отправляем запрос
                val response = ContactsApi.updateAvatar(filePart)
                if (response.isSuccessful) {
                    val newAvatar = response.body()
                    _avatar.value = newAvatar
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
            }
        }
    }

}