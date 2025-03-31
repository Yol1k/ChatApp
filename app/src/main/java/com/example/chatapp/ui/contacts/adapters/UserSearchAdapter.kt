package com.example.chatapp.ui.contacts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ui.contacts.Contact
import com.example.chatapp.R
import com.example.chatapp.ui.contacts.AddContact

class UserSearchAdapter(
    private var users: List<Contact>,
    private val onAddClick: (AddContact) -> Unit,

) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactName: TextView = itemView.findViewById(R.id.contactName)
        private val addButton: Button = itemView.findViewById(R.id.AddContactButton)

        fun bind(user: Contact) {
            contactName.text = user.name
            addButton.setOnClickListener {
                val addButton = AddContact(userId = user.id)
                onAddClick(addButton)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<Contact>) {
        users = newUsers
        notifyDataSetChanged()
    }

}