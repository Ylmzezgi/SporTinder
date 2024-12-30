package com.ezgiyilmaz.sporfinder.pages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.Adapters.ChatsAdapter
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityProfilePageBinding
import com.ezgiyilmaz.sporfinder.models.PlayerMatch
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProfilePage : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var selectedPicture: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            userProfile(currentUser.uid)
        } else {
            Log.e("ProfilePage", "Kullanıcı giriş yapmamış.")
        }

        registerLauncher()
    }

    private fun userProfile(userId: String) {
        db.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.nameTextView.text = document.getString("name")
                    val image = document.getString("selectedImage")
                    Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.user)
                        .into(binding.profileImage)
                }
            }.addOnFailureListener { e ->
                Log.e("ProfilePage", "Veri alınamadı: ${e.localizedMessage}")
            }
    }

    fun loadPicture() {
        val user = auth.currentUser
        if (user != null && selectedPicture != null) {
            val uuid = UUID.randomUUID()
            val imageName = "$user$uuid.jpg"
            val reference = storage.reference
            val imageRef = reference.child("images").child(imageName)
            db.collection("user").document(user.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val selectimage = it.getString("selectedImage")
                    if (!selectimage.isNullOrEmpty()) {
                        storage.getReferenceFromUrl(selectimage).delete().addOnSuccessListener {
                            Toast.makeText(this, "Yeni profil fotoğrafı eklendi", Toast.LENGTH_LONG)
                                .show()
                        }.addOnFailureListener { e ->
                            e.localizedMessage
                        }
                    }
                }

                    // Fotoğrafı Firebase Storage'a yükle
                    imageRef.putFile(selectedPicture!!).addOnSuccessListener {
                        // Fotoğraf yüklendikten sonra, URL'yi al
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()

                            // Kullanıcı profilini güncelle
                            val profileUpdates = userProfileChangeRequest {
                                photoUri = Uri.parse(downloadUrl)
                            }

                            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Fotoğrafın URL'sini Firestore'a kaydet
                                    saveProfilePictureUrlToFirestore(user.uid, downloadUrl)
                                    Toast.makeText(this, "Profil güncellendi", Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    Log.e(
                                        "FirebaseAuth",
                                        "Profil güncellenemedi: ${task.exception?.message}"
                                    )
                                    Toast.makeText(this, "Profil güncellenemedi", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }.addOnFailureListener { exception ->
                            Log.e(
                                "FirebaseStorage",
                                "Resim URL'si alınamadı: ${exception.localizedMessage}"
                            )
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("FirebaseStorage", "Resim yüklenemedi: ${exception.localizedMessage}")
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()

                }
            }
        }
    }
    private fun saveProfilePictureUrlToFirestore(userId: String, imageUrl: String) {
        val userRef = db.collection("user").document(userId)
        userRef.update("selectedImage", imageUrl)
            .addOnSuccessListener {
                Log.d("ProfilePage", "Profil resmi Firestore'a kaydedildi.")
            }
            .addOnFailureListener { e ->
                Log.e("ProfilePage", "Profil resmi kaydedilemedi: ${e.localizedMessage}")
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()

            }
    }

    // Profil fotoğrafı seçmek için izin kontrolü
    fun profileOnClick(view: View) {
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
                Snackbar.make(binding.root, "İzin gerekli", Snackbar.LENGTH_INDEFINITE)
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
                            binding.profileImage.setImageURI(it)
                            loadPicture()
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
}
