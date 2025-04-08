package com.example.chatapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.ui.contacts.adapters.UserSearchAdapter
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel

class UserSearchViewModel : DialogFragment() {

    private lateinit var usersAdapter: UserSearchAdapter
    private lateinit var viewModel: ContactsViewModel
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_search_users, container, false)

        // Настройка RecyclerView
        recyclerView = view.findViewById(R.id.usersRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersAdapter = UserSearchAdapter(emptyList()) { user ->
            // Обработка нажатия на кнопку добавить
            viewModel.addContact(userId = user.userId)
            Toast.makeText(context, "Запрос отправлен", Toast.LENGTH_SHORT).show()
            //dismiss() // Закрыть диалог после отправки запроса
        }
        recyclerView.adapter = usersAdapter

        searchEditText = view.findViewById(R.id.searchViewContacts)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.searchUsers(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetSearchState()
    }

    companion object {
        fun newInstance(viewModel: ContactsViewModel): UserSearchViewModel {
            val fragment = UserSearchViewModel()
            fragment.viewModel = viewModel
            return fragment
        }
    }
}
