import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.ezgiyilmaz.sporfinder.constans
import com.ezgiyilmaz.sporfinder.models.Register
import com.ezgiyilmaz.sporfinder.models.Login
import com.ezgiyilmaz.sporfinder.pages.MainActivity
import com.ezgiyilmaz.sporfinder.pages.RegisterPage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


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

    fun deleteAuth(auth: FirebaseAuth) {
        auth.currentUser!!.delete()
    }


    suspend fun loginUserAuth(auth: FirebaseAuth, login: Login): String {

        try {
            val sonuc = auth.signInWithEmailAndPassword(login.email, login.password).await()

            if (sonuc.user == null) {
                return "Başarısız giriş"
            } else {
                return "Başarılı"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return e.localizedMessage ?: "Giriş başarısız"
        }

        return constans.gecersizLink
    }



}

