package com.example.chatapp.ui.contacts

data class Contact(
    val id: String,
    val name: String
)

data class ContactRequest(
    val userId: String,
    val requestId: String,
    val name: String,
    val status: String
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
