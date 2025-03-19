package com.example.chatapp.ui.contacts.view_models

import ContactsApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.data.api.RetrofitClient.retrofit
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

    fun loadContacts(){
        val contacts = ContactsApi.getContacts()
        _contacts.value = contacts
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
}