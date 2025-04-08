import com.example.chatapp.ui.contacts.AcceptContactRequest
import com.example.chatapp.ui.contacts.AddContact
import com.example.chatapp.ui.contacts.Contact
import com.example.chatapp.ui.contacts.ContactRequest
import com.example.chatapp.ui.contacts.DeclineContactRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ContactsApi {

    @GET("contacts")
    suspend fun getContacts(): List<Contact>

    @GET("/contacts/in_requests")
    suspend fun getInRequests(): List<ContactRequest>

    @GET("/contacts/out_requests")
    suspend fun getOutRequests(): List<ContactRequest>

    @POST("/contacts/add")
    suspend fun addContact(@Body request: AddContact)

    @POST("/contacts/accept")
    suspend fun acceptRequest(@Body request: AcceptContactRequest): Response<Unit>

    @POST("/contacts/decline")
    suspend fun declineRequest(@Body request: DeclineContactRequest)

    @GET("/users/search")
    suspend fun searchUsers(
        @Query("search") query: String,
        @Query("limit") limit: Int? = null,
    ): List<Contact>

    @Multipart
    @PATCH("/users/update/avatar")
    suspend fun updateAvatar(@Part file: MultipartBody.Part): Response<String>
}