import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemIncomingRequestBinding
import com.example.chatapp.ui.contacts.api.ContactRequest

class IncomingRequestsAdapter(
    var requests: List<ContactRequest>,
    private val onAccept: (ContactRequest) -> Unit,
    private val onDecline: (ContactRequest) -> Unit
) : RecyclerView.Adapter<IncomingRequestsAdapter.ViewHolder>() {

    inner class ViewHolder( private val binding: ItemIncomingRequestBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: ContactRequest) {
            with(binding) {
                senderName.text = request.name

                Glide.with(itemView.context)
                    .load(request.avatar)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(incomingRequestsAvatar)


                acceptButton.setOnClickListener {
                    onAccept(request)
                }

                declineButton.setOnClickListener {
                    onDecline(request)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncomingRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    fun updateRequests(newRequests: List<ContactRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = requests.size
}