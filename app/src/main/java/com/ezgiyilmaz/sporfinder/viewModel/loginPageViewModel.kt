package com.ezgiyilmaz.sporfinder.viewModel

import FirebaseUserHelper
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ezgiyilmaz.sporfinder.models.Login
import com.ezgiyilmaz.sporfinder.pages.HomePage
import com.ezgiyilmaz.sporfinder.pages.RegisterPage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class loginPageViewModel : ViewModel() {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var resultAuth: String

    init {
        // Firebase'i başlatma
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }


    suspend fun signupOnClick(email: String, password: String): String {
        auth.signOut()
        val message = when {
            email.isNullOrEmpty() -> "Email alanı boş olamaz"
            password.isNullOrEmpty() -> "şifre alanı boş olamaz"
            else -> null
        }
        return message ?: run {
            val login = Login(email, password)
            val result = loginUser(login)
            result
        }
    }

    suspend fun loginUser(login: Login): String {
        val userHelper = FirebaseUserHelper()
        resultAuth = userHelper.loginUserAuth(auth, login)
        if (resultAuth.equals("Başarılı")) {
            val sonuc = isEmailVerified()
            if (sonuc) {
                return "Giriş Başarılı"

            } else {
                emailSend()
                return "Doğrulama kodu emaile gönderildi"
            }

        } else {
            return resultAuth
        }
    }

    fun registerIntent(context: Context) {
        val intent = Intent(context, RegisterPage::class.java)
        context.startActivity(intent)
    }

    fun homeIntent(context: Context) {
        val intent = Intent(context, HomePage::class.java)
        context.startActivity(intent)
    }

    fun emailSend() {
        val user = auth.currentUser
        user?.let {
            if (!it.isEmailVerified) {
                it.sendEmailVerification().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "epostas gönderildi.")
                    } else {
                        Log.e("TAG", "gönderilirken hata oluştu.", task.exception)
                    }

                }
            }
        }
    }

    fun isEmailVerified(): Boolean {
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
}