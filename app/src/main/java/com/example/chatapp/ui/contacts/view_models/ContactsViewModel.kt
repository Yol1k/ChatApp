package com.example.chatapp.ui.contacts.view_models

import ContactsApi
import android.util.Log
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
import kotlinx.coroutines.launch

class ContactsViewModel(val ContactsApi: ContactsApi) : ViewModel() {

    companion object {
        fun getViewModelFactory(ContactsApi: ContactsApi): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ContactsViewModel(
                        ContactsApi = ContactsApi
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

    fun loadContacts(){
        viewModelScope.launch {
            try {
                val contacts = ContactsApi.getContacts()
                _contacts.value = contacts
            } catch (e: Exception) {
                _contacts.value = emptyList() // Очистить список в случае ошибки
            }
        }
    }

    fun loadIncomingRequests(){
        viewModelScope.launch {
            val requests = ContactsApi.getInRequests()
            _incomingRequests.value = requests
        }
    }

    fun loadOutgoingRequests(){
        viewModelScope.launch {
            val requests = ContactsApi.getOutRequests()
            _outgoingRequests.value = requests
        }
    }

    fun addContact(userId: String) {
        viewModelScope.launch {
            val request = AddContact(userId)
            ContactsApi.addContact(request)
        }
    }

    fun acceptContactRequest(requestId: String) {
        viewModelScope.launch {
            val request = AcceptContactRequest(requestId)
            ContactsApi.acceptRequest(request)
            loadIncomingRequests()
        }
    }

    fun declineContactRequest(requestId: String) {
        viewModelScope.launch {
            val request = DeclineContactRequest(requestId)
            ContactsApi.declineRequest(request)
            loadIncomingRequests()
        }
    }
    fun searchUsers(query: String, limit: Int? = null) {
        viewModelScope.launch {
            try {
                val results = ContactsApi.searchUsers(query, limit)
                Log.d("SearchUsers", "API results: $results")
                _searchResults.value = results
            } catch (e: Exception) {
                // Обработка ошибки
                Log.e("SearchUsers", "Error: ${e.message}", e)
                _searchResults.value = emptyList()
            }
        }
    }
}