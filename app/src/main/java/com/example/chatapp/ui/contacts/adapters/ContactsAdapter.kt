package com.example.chatapp.ui.contacts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ui.contacts.Contact
import com.example.chatapp.R

class ContactsAdapter(private var contacts: List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactName: TextView = itemView.findViewById(R.id.contactName)

        private val addContactButton = itemView.findViewById<Button>(R.id.AddContactButton)

        fun bind(contact: Contact) {
            contactName.text = contact.name

            //addContactButton.setOnClickListener{ addContact(contact)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}