package com.example.chatapp.ui.contacts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ui.contacts.api.Contact
import com.example.chatapp.R
import android.widget.ImageView

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolder>(), Filterable {

    private var contacts = listOf<Contact>()

    private var filteredContacts = mutableListOf<Contact>()

    private val placeholderAvatar = R.drawable.ic_person

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        filteredContacts = newContacts.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val contactName: TextView = itemView.findViewById(R.id.contactName)
        private val contactAvatar: ImageView = itemView.findViewById(R.id.avatarImageView)

        fun bind(contact: Contact) {
            contactName.text = contact.name

            Glide.with(itemView.context)
                .load(contact.avatar) // URL аватара из объекта Contact
                .placeholder(placeholderAvatar) // Заглушка, если аватар не загружен
                .error(placeholderAvatar) // Заглушка при ошибке загрузки
                .circleCrop() // Делаем аватар круглым
                .into(contactAvatar)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredContacts[position])
    }

    override fun getItemCount(): Int = filteredContacts.size


    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val results = FilterResults()
            val filtered: MutableList<Contact> = mutableListOf()

            if (constraint.isNullOrEmpty()) {
                filtered.addAll(contacts)
            } else {
                for (user in contacts) {
                    if (user.name.contains(constraint, true)) {
                        filtered.add(user)
                    }
                }
            }

            results.values = filtered
            results.count = filtered.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredContacts = results?.values as? MutableList<Contact> ?: mutableListOf()
            notifyDataSetChanged()
        }
    }

}