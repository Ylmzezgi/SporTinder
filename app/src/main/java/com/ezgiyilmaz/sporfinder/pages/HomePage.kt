package com.ezgiyilmaz.sporfinder.pages

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityHomePageBinding
import com.ezgiyilmaz.sporfinder.util.constants
import com.ezgiyilmaz.sporfinder.viewModel.HomePageAdapter
import com.ezgiyilmaz.sporfinder.viewModel.HomePageViewModel
import com.ezgiyilmaz.sporfinder.viewModel.LocationPickerViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var homeAdapter: HomePageAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var homeViewModel: HomePageViewModel
    var selected = "player"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        homeViewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        fillList()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.radioRival.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selected = "rival"
              fillList()
            } else {
                selected = "player"
                fillList()
            }
        }

    }
    fun fillList(){
        GlobalScope.launch(Dispatchers.IO) {
            val adapter=homeViewModel.ClickPlayerViewModel(selected)
            withContext(Dispatchers.Main){
                binding.recyclerView.adapter=adapter
            }
        }
    }

}
