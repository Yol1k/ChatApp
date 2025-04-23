package com.example.chatapp.ui.fragments
import ContactsApi
import com.example.chatapp.ui.contacts.dialogs.IncomingRequestsDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatapp.databinding.FragmentContactsBinding
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.databinding.DialogSearchUsersBinding
import com.example.chatapp.ui.contacts.adapters.ContactsAdapter
import com.example.chatapp.ui.contacts.dialogs.UserSearchDialogFragment

class ContactsFragment: Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactsAdapter: ContactsAdapter

    private val contactsApi by lazy {
        RetrofitClient.create(requireContext(), ContactsApi::class.java)
    }

    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModel.getViewModelFactory(contactsApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(context)
        contactsAdapter = ContactsAdapter()
        binding.contactsRecyclerView.adapter = contactsAdapter

        viewModel.loadContacts()

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactsAdapter.updateContacts(contacts)
        }

        viewModel.incomingRequests.observe(viewLifecycleOwner) { requests ->
        }

        viewModel.outgoingRequests.observe(viewLifecycleOwner) { requests ->
        }

        binding.searchUsersButton.setOnClickListener {
            val dialog = UserSearchDialogFragment.newInstance()
            dialog.show(parentFragmentManager, "SearchUsersDialog")
        }

        binding.incomingRequestsButton.setOnClickListener {
            val fragment = IncomingRequestsDialogFragment.newInstance()
            fragment.show(parentFragmentManager, "incoming_requests_dialog")
        }

        binding.searchViewContacts.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                contactsAdapter.filter.filter(newText)
                return true
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}