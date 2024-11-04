import com.ezgiyilmaz.sporfinder.models.Register
import com.ezgiyilmaz.sporfinder.models.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUserHelper {

    suspend fun registerUserFirestore(
        user: Register,
        db: FirebaseFirestore,
        documentId: String
    ): String {
        return try {
            db.collection("user").document(documentId).set(user).await()
            "Başarılı" // İşlem başarılı ise bu mesaj döndürülür
        } catch (e: Exception) {
            e.localizedMessage ?: "Hata oluştu" // İşlem başarısızsa hata mesajını döndürür
        }

    }

    suspend fun registerUserAuth(auth: FirebaseAuth, login: Login): String {
        return try {
            auth.createUserWithEmailAndPassword(login.email, login.password).await()
            "Başarılı"
        } catch (e: Exception) {
            e.localizedMessage ?: "Hata oluştu" // İşlem başarısızsa hata mesajını döndürür

        }
    }

    fun deleteAuth(auth: FirebaseAuth){
        auth.currentUser!!.delete()
    }
}

