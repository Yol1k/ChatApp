package com.example.chatapp.ui.contacts.dialogs

import ContactsApi
import IncomingRequestsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.data.api.RetrofitClient
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel
import com.example.chatapp.databinding.DialogIncomingRequestsBinding

class IncomingRequestsDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = IncomingRequestsDialogFragment()
    }

    private var _binding: DialogIncomingRequestsBinding? = null
    private val binding get() = _binding!!

    private val contactsApi by lazy {
        RetrofitClient.create(requireContext(), ContactsApi::class.java)
    }
    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModel.getViewModelFactory(contactsApi)
    }

    private lateinit var adapter: IncomingRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogIncomingRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadRequests()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupRecyclerView() {
        adapter = IncomingRequestsAdapter(
            requests = emptyList(),
            onAccept = { request ->
                viewModel.acceptContactRequest(request.requestId)
                showText("Запрос принят")
            },
            onDecline = { request ->
                viewModel.declineContactRequest(request.requestId)
                showText("Запрос отклонен")
            }
        )

        binding.incomingRequestsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@IncomingRequestsDialogFragment.adapter
            setHasFixedSize(true)
        }

    }

    private fun showText(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun loadRequests() {
        viewModel.incomingRequests.observe(viewLifecycleOwner) { requests ->
            requests?.let {
                adapter.updateRequests(it)
            }
        }
        viewModel.loadIncomingRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}