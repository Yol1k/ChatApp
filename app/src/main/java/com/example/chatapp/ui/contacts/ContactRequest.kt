package com.example.chatapp.ui.contacts

data class Contact(
    val id: String,
    val name: String,
    val login: String,
    val avatar: String
)

data class ContactRequest(
    val userId: String,
    val requestId: String,
    val name: String,
    val login: String,
    val status: String,
    val avatar: String
)

data class UserResponse(
    var id: String,
    val name: String,
    val login: String,
    val avatar: String,
    var isInContacts: Boolean,
    var hasPendingRequest: Boolean,
    var incomeRequestId: String
)

data class AddContact(
    val userId: String
)

data class AcceptContactRequest(
    val requestId: String
)

data class DeclineContactRequest(
    val requestId: String
)