package com.example.chatapp.ui.contacts.dialogs

import ContactsApi
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.databinding.DialogSearchUsersBinding
import com.example.chatapp.ui.contacts.adapters.UserSearchAdapter
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel
import kotlinx.coroutines.Job
import kotlin.getValue

class UserSearchDialogFragment : DialogFragment() {

    private var _binding: DialogSearchUsersBinding? = null
    private val binding get() = _binding!!

    private var searchJob: Job? = null

    companion object {
        fun newInstance(): UserSearchDialogFragment {
            return UserSearchDialogFragment()
        }
    }

    private val contactsApi by lazy {
        RetrofitClient.create(requireContext(), ContactsApi::class.java)
    }

    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModel.getViewModelFactory(contactsApi)
    }

    private val usersAdapter by lazy {
        UserSearchAdapter(emptyList()) { user ->
            viewModel.addContact(userId = user.userId)
            Toast.makeText(context, "Запрос отправлен", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogSearchUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeSearchResults()
        setupDialogSize()

    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchViewContacts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.trim()?.let { query ->
                    if (query.isNotEmpty()) {
                        viewModel.searchUsers(query)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { users ->
            usersAdapter.updateUsers(users ?: emptyList())
        }
    }

    private fun setupDialogSize() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null
        viewModel.resetSearchState()
    }

}
