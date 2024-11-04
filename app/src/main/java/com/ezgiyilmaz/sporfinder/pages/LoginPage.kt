package com.ezgiyilmaz.sporfinder.pages

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityLoginPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun signupOnClick(view: View) {
        val email = binding.EmailAddressEditText.text.toString()
        val password = binding.passwordPasswordEditText.text.toString()

        if (email.equals("") && password.equals("")) {
            Toast.makeText(this, "Email ve şifre giriniz", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                Intent(this, MainActivity::class.java).also {
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
        }
    }

    fun registerOnClick(view:View){
        Intent(this, RegisterPage::class.java).also {  startActivity(intent)
            finish()
        }
    }

}