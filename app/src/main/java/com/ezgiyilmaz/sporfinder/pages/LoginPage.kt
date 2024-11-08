package com.ezgiyilmaz.sporfinder.pages

import FirebaseUserHelper // FirebaseUserHelper import edilmiştir.
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityLoginPageBinding
import com.ezgiyilmaz.sporfinder.models.Login
import com.ezgiyilmaz.sporfinder.viewModel.loginPageViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: loginPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ViewModel'i başlatıyoruz
        viewModel = ViewModelProvider(this).get(loginPageViewModel::class.java)

    }

    // Kayıt olma butonuna tıklama işlemi
    fun signupOnClick(view: View) {
        val email = binding.EmailAddressEditText.text.toString()
        val password = binding.passwordPasswordEditText.text.toString()

        // Kayıt işlemi yapılır
        GlobalScope.launch(Dispatchers.IO) {
            val result = viewModel.signupOnClick(email, password) // signupOnClick fonksiyonu çağrılır
            withContext(Dispatchers.Main) {
                // Kayıt sonucuna göre kullanıcıya Toast mesajı gösterilir
                Toast.makeText(this@LoginPage, result, Toast.LENGTH_LONG).show()
                if(result=="Giriş Başarılı"){
                    viewModel.homeIntent(this@LoginPage)
                }
            }
        }
    }

    // Kayıt sayfasına yönlendirme işlemi
    fun registerOnClick(view: View) {
        val intent = Intent(this, RegisterPage::class.java) // RegisterPage aktivitesine yönlendirme
        startActivity(intent)
        finish() // Mevcut activity'i sonlandırıyoruz
    }




    fun kayıtOlmadanOnClick(view: View){
        viewModel.homeIntent(this)
    }
    }
