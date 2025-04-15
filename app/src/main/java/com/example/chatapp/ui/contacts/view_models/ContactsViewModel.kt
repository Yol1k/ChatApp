package com.example.chatapp.ui.contacts.view_models

import ContactsApi
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.ui.contacts.AcceptContactRequest
import com.example.chatapp.ui.contacts.AddContact
import com.example.chatapp.ui.contacts.Contact
import com.example.chatapp.ui.contacts.ContactRequest
import com.example.chatapp.ui.contacts.DeclineContactRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ContactsViewModel(val contactsApi: ContactsApi) : ViewModel() {

    companion object {
        fun getViewModelFactory(contactsApi: ContactsApi): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ContactsViewModel(
                        contactsApi = contactsApi
                    )
                }
            }}


    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    private val _incomingRequests = MutableLiveData<List<ContactRequest>>()
    val incomingRequests: LiveData<List<ContactRequest>> get() = _incomingRequests

    private val _outgoingRequests = MutableLiveData<List<ContactRequest>>()
    val outgoingRequests: LiveData<List<ContactRequest>> get() = _outgoingRequests

    private val _searchResults = MutableLiveData<List<Contact>>()
    val searchResults: LiveData<List<Contact>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private var searchJob: Job? = null

    private val _avatarUrl = MutableLiveData<String?>()
    val avatarUrl: LiveData<String?> = _avatarUrl

    fun loadContacts(){
        viewModelScope.launch {
            try {
                val contacts = contactsApi.getContacts()
                _contacts.value = contacts
            } catch (e: Exception) {
                _contacts.value = emptyList() // Очистить список в случае ошибки
            }
        }
    }

    fun loadIncomingRequests(){
        viewModelScope.launch {
            val requests = contactsApi.getInRequests()
            _incomingRequests.value = requests
        }
    }

    fun loadOutgoingRequests(){
        viewModelScope.launch {
            val requests = contactsApi.getOutRequests()
            _outgoingRequests.value = requests
        }
    }

    fun addContact(userId: String) {
        viewModelScope.launch {
            val request = AddContact(userId)
            contactsApi.addContact(request)
            loadOutgoingRequests()
        }
    }

    fun acceptContactRequest(requestId: String) {
        viewModelScope.launch {
            val request = AcceptContactRequest(requestId)
            contactsApi.acceptRequest(request)
            loadIncomingRequests()
            loadContacts()
        }
    }

    fun declineContactRequest(requestId: String) {
        viewModelScope.launch {
            val request = DeclineContactRequest(requestId)
            contactsApi.declineRequest(request)
            loadIncomingRequests()
        }
    }

    fun searchUsers(query: String, limit: Int? = null) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList() // Показываем пустой список, если запрос пустой
            return
        }

        searchJob?.cancel() // Отменяем предыдущий запрос, если он есть
        searchJob = viewModelScope.launch {
            _isLoading.value = true // Показываем индикатор загрузки
            delay(300) // Задержка 300 мс перед выполнением запроса
            try {
                val results = contactsApi.searchUsers(query, limit)
                Log.d("SearchUsers", "API results: $results")
                _searchResults.value = results
            } catch (e: Exception) {
                // Обработка ошибки
                Log.e("SearchUsers", "Error: ${e.message}", e)
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false // Скрываем индикатор загрузки
            }
        }
    }

    fun resetSearchState() {
        _searchResults.value = emptyList()
        _errorMessage.value = null
        _isLoading.value = false
    }

}