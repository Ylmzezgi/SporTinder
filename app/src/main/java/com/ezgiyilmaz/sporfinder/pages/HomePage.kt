package com.ezgiyilmaz.sporfinder.pages

import PagingViewModel
import RivalAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityHomePageBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var rivalViewModel: PagingViewModel
    private lateinit var rivalAdapter: RivalAdapter

    var selected = "player"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomePage başlatılıyor.")
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel oluşturuluyor
        rivalViewModel = ViewModelProvider(this).get(PagingViewModel::class.java)
        println("PagingViewModel oluşturuldu.")

        setAdapter()
        fillList()

        // Edge-to-Edge özelliği etkinleştiriliyor
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // RadioButton dinleyicisi ayarlanıyor
        binding.radioRival.setOnCheckedChangeListener { _, isChecked ->
            println("RadioButton durumu değiştirildi. Seçili: ${if (isChecked) "rival" else "player"}")
            setAdapter()
            if (isChecked) {
                selected = "rival"
                fillListRival()
            } else {
                selected = "player"
                fillList()
            }
        }
    }

    // Oyuncu listesini doldurur
    fun fillList() {
        println("Oyuncu listesi dolduruluyor.")
        lifecycleScope.launch {
            rivalViewModel.playerPagingData.collectLatest {
                println("Oyuncu listesi güncelleniyor.")
                rivalAdapter.submitData(it)
            }
        }
    }

    // Rakip listesini doldurur
    fun fillListRival() {
        println("Rakip listesi dolduruluyor.")
        lifecycleScope.launch {
            rivalViewModel.rivalPagingData.collectLatest {
                println("Rakip listesi güncelleniyor.")
                rivalAdapter.submitData(it)
            }
        }
    }

    // RecyclerView adaptörünü ayarlar
    fun setAdapter() {
        println("RecyclerView adaptörü ayarlanıyor.")
        rivalAdapter = RivalAdapter()

        // RecyclerView ayarları
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomePage)
            adapter = rivalAdapter
        }
        println("RecyclerView adaptörü ayarlandı.")
    }
}
