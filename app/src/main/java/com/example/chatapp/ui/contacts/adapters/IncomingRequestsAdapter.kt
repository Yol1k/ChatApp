import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ui.contacts.ContactRequest
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentContactsBinding

class IncomingRequestsAdapter(
    var requests: List<ContactRequest>,
    private val onAccept: (ContactRequest) -> Unit,
    private val onDecline: (ContactRequest) -> Unit
) : RecyclerView.Adapter<IncomingRequestsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderName: TextView = itemView.findViewById(R.id.senderName)
        private val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        private val declineButton: Button = itemView.findViewById(R.id.declineButton)
        private val placeholderAvatar = R.drawable.ic_person
        private val contactAvatar: ImageView = itemView.findViewById(R.id.incomingRequestsAvatar)

        fun bind(request: ContactRequest) {
            senderName.text = request.name

            Glide.with(itemView.context)
                .load(request.avatar) // URL аватара из объекта Contact
                .placeholder(placeholderAvatar) // Заглушка, если аватар не загружен
                .error(placeholderAvatar) // Заглушка при ошибке загрузки
                .circleCrop() // Делаем аватар круглым
                .into(contactAvatar)


            acceptButton.setOnClickListener {
                onAccept(request) // Обработка принятия запроса
            }

            declineButton.setOnClickListener {
                onDecline(request) // Обработка отклонения запроса
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incoming_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    fun updateRequests(newRequests: List<ContactRequest>) {
        requests = newRequests // Обновляем список
        notifyDataSetChanged() // Уведомляем адаптер об изменениях
    }

    override fun getItemCount(): Int = requests.size
}