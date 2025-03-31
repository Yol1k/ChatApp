import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ui.contacts.view_models.ContactsViewModel
import com.example.chatapp.R
import com.example.chatapp.databinding.DialogIncomingRequestsBinding
import com.example.chatapp.ui.fragments.UserSearchViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IncomingRequestsFragment : DialogFragment() {
    private lateinit var binding: DialogIncomingRequestsBinding
    private lateinit var adapter: IncomingRequestsAdapter
    private lateinit var viewModel: ContactsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogIncomingRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeRequests()
        loadRequests()
    }

    private fun setupRecyclerView() {
        adapter = IncomingRequestsAdapter(
            requests = emptyList(),
            onAccept = { request ->
                viewModel.acceptContactRequest(request.requestId)
                //Toast.makeText(context, "Запрос принят", Toast.LENGTH_SHORT).show()
            },
            onDecline = { request ->
                viewModel.declineContactRequest(request.requestId)
                //Toast.makeText(context, "Запрос отклонен", Toast.LENGTH_SHORT).show()
            }
        )

        binding.incomingRequestsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@IncomingRequestsFragment.adapter
        }
    }

    private fun observeRequests() {
        viewModel.incomingRequests.observe(viewLifecycleOwner) { requests ->
            adapter.updateRequests(requests)
        }
    }

    private fun loadRequests() {
        viewModel.loadIncomingRequests()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        fun newInstance(viewModel: ContactsViewModel): IncomingRequestsFragment {
            val fragment = IncomingRequestsFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }
}