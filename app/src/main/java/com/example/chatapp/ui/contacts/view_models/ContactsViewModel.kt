package com.example.chatapp.ui.contacts.view_models

import ContactsApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatapp.ui.contacts.api.AcceptContactRequest
import com.example.chatapp.ui.contacts.api.AddContact
import com.example.chatapp.ui.contacts.api.Contact
import com.example.chatapp.ui.contacts.api.ContactRequest
import com.example.chatapp.ui.contacts.api.DeclineContactRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val searchUsers: (String) -> Unit = debounce(
        waitMs = 300L,
        coroutineScope = viewModelScope,
        destinationFunction = this::searchUsers
    )

    private val _avatarUrl = MutableLiveData<String?>()
    val avatarUrl: LiveData<String?> = _avatarUrl

    private val _contactsState = MutableLiveData<ContactsState>()
    val contactsState: LiveData<ContactsState> = _contactsState

    fun loadContacts(){
        viewModelScope.launch {
            _contactsState.value = ContactsState.Loading
            try {
                val contacts = contactsApi.getContacts()
                _contacts.value = contacts
                _contactsState.value = ContactsState.Success
            } catch (e: Exception) {
                _contacts.value = emptyList()
                _contactsState.value = ContactsState.Error("Список контактов не получен")
            }
        }
    }

    fun loadIncomingRequests(){
        viewModelScope.launch {
            _contactsState.value = ContactsState.Loading
            val requests = contactsApi.getInRequests()
            _contactsState.value = ContactsState.Success
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

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _contactsState.value = ContactsState.Loading
            try {
                val response = contactsApi.searchUsers(query)
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?: emptyList()
                    _contactsState.value = ContactsState.Success
                } else {
                    _searchResults.value = emptyList()
                    _contactsState.value = ContactsState.Success
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _contactsState.value = ContactsState.Error("Список пользователей не получен")
            }
        }
    }

    fun resetSearchState() {
        _searchResults.value = emptyList()
    }

    fun <T> debounce(
        waitMs: Long = 300L,
        coroutineScope: CoroutineScope,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(waitMs)
                destinationFunction(param)
            }
        }}

    fun <T> throttleFirst(
        skipMs: Long = 300L,
        coroutineScope: CoroutineScope,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var throttleJob: Job? = null
        return { param: T ->
            if (throttleJob?.isCompleted != false) {
                throttleJob = coroutineScope.launch {
                    destinationFunction(param)
                    delay(skipMs)
                }
            }
        }
    }

    fun <T> throttleLatest(
        intervalMs: Long = 300L,
        coroutineScope: CoroutineScope,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var throttleJob: Job? = null
        var latestParam: T
        return { param: T ->
            latestParam = param
            if (throttleJob?.isCompleted != false) {
                throttleJob = coroutineScope.launch {
                    delay(intervalMs)
                    destinationFunction(latestParam)
                }
            }
        }
    }

}
sealed class ContactsState {
    object Loading : ContactsState()
    object Success: ContactsState()
    data class Error(val message: String) : ContactsState()
}
