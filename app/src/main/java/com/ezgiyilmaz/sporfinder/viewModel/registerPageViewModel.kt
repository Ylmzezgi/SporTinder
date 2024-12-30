package com.ezgiyilmaz.sporfinder.viewModel

import FirebaseUserHelper
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.ezgiyilmaz.sporfinder.models.Login
import com.ezgiyilmaz.sporfinder.models.Register
import com.ezgiyilmaz.sporfinder.models.city
import com.ezgiyilmaz.sporfinder.serviceHelper.getService
import com.ezgiyilmaz.sporfinder.services.retrofitService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.await
import java.util.UUID

class registerPageViewModel : ViewModel() {


    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    init {
        // Firebase'i başlatma
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }



    suspend fun saveClick(
        name: String,
        surname: String,
        userName: String,
        cityLocation: String,
        townShip: String,
        email: String,
        password: String,
        passwordAgain: String,
        selectedImage: Uri?
    ): String {

        if (name.isNullOrEmpty())
            return "İsim alanı boş olamaz"

        if (surname.isNullOrEmpty())
            return "soyisim alanı boş olamaz"

        if (userName.isNullOrEmpty())
            return "kullanıcıadı alanı boş olamaz"

        if (cityLocation.isNullOrEmpty())
            return "İl alanı boş olamaz"

        if (townShip.isNullOrEmpty())
            return "ilçe alanı boş olamaz"

        if (email.isNullOrEmpty())
            return "email alanı boş olamaz"

        if (password.isNullOrEmpty())
            return "şifre alanı boş olamaz"

        if (passwordAgain.isNullOrEmpty())
            return "Şifreyi tekrar giriniz"

        if (!password.equals(passwordAgain))
            return "Şifreler uyuşmuyor!!"



        val login = Login(email, password)
        val register = Register(name, surname, userName, cityLocation, townShip,
            selectedImage!!.toString()
        )

        val result = createUser(login, register)
        return result

    }

    suspend fun createUser(login: Login, register: Register): String {
        val firebaseUserHelper = FirebaseUserHelper()
        val resultAuth = firebaseUserHelper.registerUserAuth(auth, login)
        if (resultAuth.equals("Başarılı")) {
            val documentId = auth.currentUser!!.uid
            val resultFirestore = firebaseUserHelper.registerUserFirestore(register, db, documentId)
            if (!resultFirestore.equals("Başarılı")) {
                firebaseUserHelper.deleteAuth(auth)
            }
            return resultFirestore
        }
        return "kayıt edilemedi"
    }
}
