package com.example.chatapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.ui.contacts.adapters.UserSearchAdapter
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel

class UserSearchViewModel : DialogFragment() {

    private lateinit var usersAdapter: UserSearchAdapter
    private lateinit var viewModel: ContactsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_search_users, container, false)

        // Настройка RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersAdapter = UserSearchAdapter(emptyList()) { user ->
            // Обработка нажатия на пользователя (например, отправка запроса)
            viewModel.addContact(user.id)
            dismiss() // Закрыть диалог после отправки запроса
        }
        recyclerView.adapter = usersAdapter

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            Log.d("SearchUsers", "Search query: $query")
            viewModel.searchUsers(query) // Выполняем поиск по введенному запросу
        }

        // Наблюдение за результатами поиска
        viewModel.searchResults.observe(viewLifecycleOwner) { users ->
            Log.d("SearchUsers", "Observed users: $users")
            users?.let {
                usersAdapter.updateUsers(it)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Установим размер равный MATCH_PARENT по ширине и высоте
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
    }

    companion object {
        fun newInstance(viewModel: ContactsViewModel): UserSearchViewModel {
            val fragment = UserSearchViewModel()
            fragment.viewModel = viewModel
            return fragment
        }
    }
}
