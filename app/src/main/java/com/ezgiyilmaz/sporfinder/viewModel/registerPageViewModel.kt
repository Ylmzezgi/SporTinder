package com.ezgiyilmaz.sporfinder.viewModel

import FirebaseUserHelper
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.await

class registerPageViewModel : ViewModel() {
    private lateinit var apiInterface: getService
    lateinit var cities: List<String>
    lateinit var townShips: List<String>
    lateinit var cityId: List<city>

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    init {
        // Firebase'i başlatma
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun getApiInterface() {
        apiInterface = retrofitService.getInstance().create(getService::class.java)
    }


    suspend fun getCity() {
        val response = apiInterface.getCity().await() // Execute the call once
        cities = response.orEmpty()
            .firstOrNull { it.name == "il" }?.data  // Access the "il" data
            ?.mapNotNull { it.name.trim() } ?: emptyList()

        cityId = response.orEmpty()
            .firstOrNull { it.name == "il" }?.data  // Access the "il" data
            ?.mapNotNull { city(it.id, it.name) } ?: emptyList()

        cities = cities.sorted()
    }


    suspend fun getTownShip(ilId: String?) {
        val response = apiInterface.getTownShip().await() // Execute the call once
        townShips = response.orEmpty()
            .firstOrNull { it.name == "ilce" }?.data  // Access the "il" data
            ?.filter { it.il_id == ilId }?.map { it.name.trim() } ?: emptyList()


        townShips = townShips.sorted()
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
        val register = Register(name, surname, userName, cityLocation, townShip)

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