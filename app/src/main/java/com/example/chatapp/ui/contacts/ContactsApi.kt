import com.example.chatapp.ui.contacts.Contact
import com.example.chatapp.ui.contacts.AddContact
import com.example.chatapp.ui.contacts.AcceptContactRequest
import com.example.chatapp.ui.contacts.ContactRequest
import com.example.chatapp.ui.contacts.DeclineContactRequest
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body

interface ContactsApi {

    @GET("contacts")
    fun getContacts(): List<Contact>

    @GET("/contacts/in_request")
    fun getInRequests(): List<ContactRequest>

    @GET("/contacts/out_request")
    fun getOutRequests(): List<ContactRequest>

    @POST("/contacts/add")
    fun addContact(@Body request: AddContact)

    @POST("/contacts/accept")
    fun acceptRequest(@Body request: AcceptContactRequest)

    @POST("/contacts/decline")
    fun declineRequest(@Body request: DeclineContactRequest)
}