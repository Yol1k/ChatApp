package com.example.chatapp.ui.fragments
import ContactsApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatapp.databinding.FragmentContactsBinding
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.data.api.RetrofitClient.retrofit

class ContactsFragment: Fragment() {

    private val ContactsApi: ContactsApi by lazy {
        retrofit.create(ContactsApi::class.java)
    }

    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModel.getViewModelFactory(ContactsApi)
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

        viewModel.loadContacts()

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
        }

        viewModel.incomingRequests.observe(viewLifecycleOwner) { requests ->
        }

        viewModel.outgoingRequests.observe(viewLifecycleOwner) { requests ->
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}