package com.ezgiyilmaz.sporfinder.pages

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityRegisterPageBinding
import com.ezgiyilmaz.sporfinder.models.city
import com.ezgiyilmaz.sporfinder.serviceHelper.getService
import com.ezgiyilmaz.sporfinder.services.retrofitService
import com.ezgiyilmaz.sporfinder.viewModel.LocationPickerViewModel
import com.ezgiyilmaz.sporfinder.viewModel.registerPageViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import java.util.UUID

class RegisterPage : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var viewModel: registerPageViewModel
    private lateinit var locationPicker: LocationPickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this).get(registerPageViewModel::class.java)
        locationPicker = ViewModelProvider(this).get(LocationPickerViewModel::class.java)
        locationPicker.getApiInterface()
        viewModel.db= Firebase.firestore
        viewModel.auth=Firebase.auth
        GlobalScope.launch(Dispatchers.IO) {
            locationPicker.getCity()
            withContext(Dispatchers.Main) {
                fillCitySpinner()
            }
        }

        binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Seçilen öğe burada işlenebilir
                val selectedCity = parent.getItemAtPosition(position).toString()
                println("Seçilen Şehir: $selectedCity")

                val selected = locationPicker.cityId.filter { city: city -> city.name == selectedCity }

                GlobalScope.launch(Dispatchers.IO) {
                    locationPicker.getTownShip(selected.firstOrNull()?.id)
                    withContext(Dispatchers.Main) {
                        fillTownShipSpinner()
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Hiçbir şey seçilmediğinde yapılacak işlemler
            }
        }

    }

    suspend fun fillCitySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationPicker.cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mySpinner.adapter = adapter
    }


    suspend fun fillTownShipSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationPicker.townShips)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter
    }


    fun saveOnClick(view: View) {
        val name = binding.nameEditText.text.toString()
        val surname = binding.surnameEditText.text.toString()
        val userName = binding.userNameEditText.text.toString()
        val city = binding.mySpinner.selectedItem.toString()
        val townShip = binding.locationSpinner.selectedItem.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val againPassword = binding.againPasswordEditText.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val result = viewModel.saveClick(
                name,
                surname,
                userName,
                city,
                townShip,
                email,
                password,
                againPassword
            )
            withContext(Dispatchers.Main){
                Toast.makeText(this@RegisterPage,result,Toast.LENGTH_LONG).show()
            }


        }
    }

}