package com.ezgiyilmaz.sporfinder.pages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.ByteArrayOutputStream
import java.util.UUID

class RegisterPage : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var viewModel: registerPageViewModel
    private lateinit var locationPicker: LocationPickerViewModel
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
//    var selectedBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        registerLauncher()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this).get(registerPageViewModel::class.java)
        locationPicker = ViewModelProvider(this).get(LocationPickerViewModel::class.java)
        locationPicker.getApiInterface()
        viewModel.db = Firebase.firestore
        viewModel.auth = Firebase.auth
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

                val selected =
                    locationPicker.cityId.filter { city: city -> city.name == selectedCity }

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
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, locationPicker.cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mySpinner.adapter = adapter
    }


    suspend fun fillTownShipSpinner() {
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, locationPicker.townShips)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter
    }

    fun circleImageClick(view: View) {
        binding.circleImage.visibility = View.VISIBLE
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                Snackbar.make(view, "İzin gerekli", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin ver") {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding.circleImage.setImageURI(it)
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(this, "İzin verilmedi", Toast.LENGTH_LONG).show()
                }
            }
    }

//    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
//        var width = image.width
//        var height = image.height
//
//        val bitmapRatio: Double = width.toDouble() / height.toDouble()
//        if (bitmapRatio > 1) {
//            width = maximumSize
//            val scaledHeight = width / bitmapRatio
//            height = scaledHeight.toInt()
//
//        } else {
//            height = maximumSize
//            val scaledWidh = height * bitmapRatio
//            width = scaledWidh.toInt()
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true)
//    }


    fun saveOnClick(view: View) {
        val name = binding.nameEditText.text.toString()
        val surname = binding.surnameEditText.text.toString()
        val userName = binding.userNameEditText.text.toString()
        val city = binding.mySpinner.selectedItem.toString()
        val townShip = binding.locationSpinner.selectedItem.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val againPassword = binding.againPasswordEditText.text.toString()

//        if (selectedBitmap != null) {
//            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)
//            val outputStream = ByteArrayOutputStream()
//            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
//            outputStream.toByteArray()


            GlobalScope.launch(Dispatchers.IO) {
                val result = viewModel.saveClick(
                    name,
                    surname,
                    userName,
                    city,
                    townShip,
                    email,
                    password,
                    againPassword,
                    selectedPicture!!
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterPage, result, Toast.LENGTH_LONG).show()
                }


            }
        }
    }