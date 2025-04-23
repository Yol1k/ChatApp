package com.example.chatapp.ui.contacts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ui.contacts.api.Contact
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemSearchUserBinding
import com.example.chatapp.ui.contacts.api.AddContact

class UserSearchAdapter(
    private var users: List<Contact>,
    private val onAddClick: (AddContact) -> Unit,

) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSearchUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            with(binding) {
                contactName.text = contact.name

                Glide.with(root.context)
                    .load(contact.avatar)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(contactAvatar)

                AddContactButton.setOnClickListener {
                    onAddClick(AddContact(userId = contact.id))
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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