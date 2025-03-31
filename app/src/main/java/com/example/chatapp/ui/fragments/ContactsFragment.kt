package com.example.chatapp.ui.fragments
import ContactsApi
import IncomingRequestsFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatapp.databinding.FragmentContactsBinding
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.ui.contacts.adapters.ContactsAdapter

class ContactsFragment: Fragment() {

    private lateinit var contactsAdapter: ContactsAdapter

    private val contactsApi by lazy {
        RetrofitClient.create(requireContext(), view, ContactsApi::class.java)
    }

    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModel.getViewModelFactory(contactsApi)
    }

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.contactsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        contactsAdapter = ContactsAdapter(emptyList())
        recyclerView.adapter = contactsAdapter

        viewModel.loadContacts()

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactsAdapter.updateContacts(contacts)
        }

        viewModel.incomingRequests.observe(viewLifecycleOwner) { requests ->
        }

        viewModel.outgoingRequests.observe(viewLifecycleOwner) { requests ->
        }

        binding.searchUsersButton.setOnClickListener {
            val dialog = UserSearchViewModel.newInstance(viewModel)
            dialog.show(parentFragmentManager, "SearchUsersDialog")
        }

        binding.incomingRequestsButton.setOnClickListener {
            // Создаем экземпляр фрагмента
            val fragment = IncomingRequestsFragment.newInstance(viewModel)

            // Открываем фрагмент как диалоговое окно
            fragment.show(parentFragmentManager, "incoming_requests_dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}