package com.ezgiyilmaz.sporfinder.pages

import PagingViewModel
import RivalAdapter
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.sporfinder.Adapters.PagingSource
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityHomePageBinding
import com.ezgiyilmaz.sporfinder.models.FilterCriteria
import com.ezgiyilmaz.sporfinder.models.rivalModel
import com.ezgiyilmaz.sporfinder.util.constants
import com.ezgiyilmaz.sporfinder.viewModel.LocationPickerViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var rivalViewModel: PagingViewModel
    private lateinit var rivalAdapter: RivalAdapter
    private lateinit var locationPicker: LocationPickerViewModel
    private lateinit var query: Query
    private lateinit var toggle: ActionBarDrawerToggle
    var selected = ""
    var criteria = FilterCriteria()

    val cal = Calendar.getInstance()
    private var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomePage başlatılıyor.")
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rivalViewModel = ViewModelProvider(this).get(PagingViewModel::class.java)
        println("PagingViewModel oluşturuldu.")


        locationPicker = ViewModelProvider(this).get(LocationPickerViewModel::class.java)
        locationPicker.getApiInterface()
        query = db.collection("oyuncuBul")
        setAdapter()
        fillList()
        toggle = ActionBarDrawerToggle(
            this,
            binding.main,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.main.addDrawerListener(toggle)
        toggle.syncState()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // RadioButton dinleyicisi ayarlanıyor
        binding.radioRival.setOnCheckedChangeListener { _, isChecked ->
            setAdapter()
            println("RadioButton durumu değiştirildi. Seçili: ${if (isChecked) "rival" else "player"}")
            query = db.collection("oyuncuBul")
            if (isChecked) {
                criteria = FilterCriteria()
                selected = "player"
                fillList()

            } else {
                criteria = FilterCriteria()
                selected = "rival"
                fillListRival()
            }
        }
        fillCitvAndTownships()
        showDatePicker()
        showTimePicker()
        category()
        filterButon()

        val headerview = binding.navView.getHeaderView(0)
        val radiorival: RadioButton = headerview.findViewById(R.id.radioRival)
        val lookingforSpinner: Spinner = headerview.findViewById(R.id.lookingforSpinner)


        radiorival.setOnCheckedChangeListener { buttonView, isChecked ->
            setAdapter()
            if (isChecked) {
                criteria = FilterCriteria()
                selected = "rival"

                lookingforSpinner.isEnabled = false
            } else {
                criteria = FilterCriteria()
                selected = "player"
                lookingforSpinner.isEnabled = true
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                fillSpinner(
                    constants.categories,
                    binding.navView.findViewById(R.id.CategoryTextView)
                )
            }
        }


    }

    // Oyuncu listesini doldurur
    fun fillList() {
        println("Oyuncu listesi dolduruluyor.")
        lifecycleScope.launch {
            val playerResult = rivalViewModel.pagingData(criteria, selected)
            println("Oyuncu listesi güncelleniyor.")
            playerResult.collectLatest {
                rivalAdapter.submitData(it)
            }
        }

    }

    // Rakip listesini doldurur
    fun fillListRival() {
        println("Rakip listesi dolduruluyor.")
        lifecycleScope.launch {
            rivalAdapter
            val playerResult = rivalViewModel.pagingData(criteria, selected)
            println("Oyuncu listesi güncelleniyor.")
            playerResult.collectLatest {
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

    fun fillCitvAndTownships() {
        val headerView = binding.navView.getHeaderView(0)
        val citySpinner: Spinner = headerView.findViewById(R.id.cityTextView)

        lifecycleScope.launch {
            val viewModel =
                ViewModelProvider(this@HomePage).get(LocationPickerViewModel::class.java)
            viewModel.getApiInterface()
            viewModel.getCity()
            val cityList = viewModel.cityId

            Log.d("TAG", "viewModel.getCity(): " + cityList)

            val cityAdapter = ArrayAdapter(
                this@HomePage,
                android.R.layout.simple_spinner_item,
                cityList.map { it.name }
            )
            Log.d("TAG", "cityAdapter: " + cityAdapter)
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // ADAPTERDA NASIL GÖZÜKECEĞİNİ AYARLAR
            citySpinner.adapter =
                cityAdapter// SPİNNERIN İÇİNİ CİTYADAPTER A ATADIĞIMIZ İLE DOLDURUR

            citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
//
                    val selected = cityList[position].id
                    showTownships(selected)

                    println("Şehir ID'leri: ${viewModel.cityId}")
                    Log.d("TAG", "viewModel.cityId: " + viewModel.cityId)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

    }

    fun showTownships(cityId: String) {
        val headerView = binding.navView.getHeaderView(0)
        val townShipSpinner: Spinner = headerView.findViewById(R.id.townShipTextView)

        lifecycleScope.launch {
            val viewModel =
                ViewModelProvider(this@HomePage).get(LocationPickerViewModel::class.java)
            viewModel.getApiInterface()
            viewModel.getTownShip(cityId)


            println("İlçeler: ${viewModel.townShips}")

            val townshipsAdapter = ArrayAdapter(
                this@HomePage,
                android.R.layout.simple_spinner_item,
                viewModel.townShips
            )
            townshipsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            townShipSpinner.adapter = townshipsAdapter

            // İlçe seçim işlemi
            townShipSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedTownship = viewModel.townShips[position] // İlçeyi al
                    println("Seçilen İlçe: ${selectedTownship}, ID: ${selectedTownship}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    println("Hiçbir ilçe seçilmedi.")
                }
            }
        }
    }

    fun imageOnClick(view: View) {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)

    }

    private fun showDatePicker() {

        val headerView = binding.navView.getHeaderView(0)
        val dateTextView: TextView = headerView.findViewById<TextView>(R.id.dateTextView)

        dateTextView.setText(SimpleDateFormat("dd/MM/yyyy", Locale.US).format(cal.time))

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                dateTextView.setText(sdf.format(cal.time))
                val date = sdf.parse(dateTextView.text.toString())
                if (date != null) {
                    val timestamp = Timestamp(date)
                    println("Timestamp: $timestamp")
                }
            }


        dateTextView.setOnClickListener {
            val dialog = DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
            if (dateTextView.text.isNotEmpty() && dateTextView.text != "dd/MM/yyyy") {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                dateTextView.setText(sdf.format(cal.time))
            } else {
                println("Tarih seçilmedi.")
            }



            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

    }

    private fun showTimePicker() {

        val headerview = binding.navView.getHeaderView(0)
        val timeTextView: TextView = headerview.findViewById(R.id.timeTextView)
        timeTextView.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
            }

            if (timeTextView.text.isNotEmpty()) {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeTextView.text = timeFormat.format(cal.time)
            } else {
                println("saat seçilmedi")
            }


            TimePickerDialog(
                this, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true
            ).show() // 24 saat formatı için true parametresi
        }


    }

    private fun category() {
        val headerView = binding.navView.getHeaderView(0)
        val categoryspinner: Spinner = headerView.findViewById(R.id.CategoryTextView)
        categoryspinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedCategory = parent.getItemAtPosition(position).toString()
                    Log.d("TAG", "onItemSelected: " + selectedCategory)
                    println(selectedCategory)
                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            var listof: List<String>
                            if (selectedCategory == "Futbol") {
                                listof = constants.positionFootball
                            } else if (selectedCategory == "Basketbol") {
                                listof = constants.positionBasketball
                            } else if (selectedCategory == "Voleybol") {
                                listof = constants.positionVoleyball
                            } else {
                                listof = constants.positionTennis
                            }
                            fillSpinner(listof, headerView.findViewById(R.id.lookingforSpinner))
                        }
                    }


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    suspend fun fillSpinner(list: List<String>, spinner: Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun getFirebaseData(criteria: FilterCriteria) {
        if (selected == "rival") {
            query = db.collection("rakipBul")
            Log.d("deneme", "rival: " + query)


        } else {
            query = db.collection("oyuncuBul")
            Log.d("deneme", "else içi: " + query)


        }
        if (criteria.category != null) {
            query = query.whereEqualTo("category", criteria.category)
            Log.d("deneme", "category: " + query)

        }
        if (criteria.lookingFor != null) {
            query = query.whereEqualTo("lookingFor", criteria.lookingFor)
            Log.d("deneme", "lookingFor: " + query)
        }

        if (criteria.city != null) {
            query = query.whereEqualTo("city", criteria.city)
            Log.d("deneme", "city: " + query)

        }
        if (criteria.townShip != null) {
            query = query.whereEqualTo("townShip", criteria.townShip)
            Log.d("deneme", "township: " + query)

        }
    }

    fun filterButon() {
        val headerview = binding.navView.getHeaderView(0)
        val filterbuton: Button = headerview.findViewById(R.id.filterButon)
        val categorySpinner = headerview.findViewById<Spinner>(R.id.CategoryTextView)
        val lookingforSpinner = headerview.findViewById<Spinner>(R.id.lookingforSpinner)
        val dateTextview = headerview.findViewById<TextView>(R.id.dateTextView)
        val timeTextview = headerview.findViewById<TextView>(R.id.timeTextView)
        val cityTextview = headerview.findViewById<Spinner>(R.id.cityTextView)
        val townshipsTextview = headerview.findViewById<Spinner>(R.id.townShipTextView)

        filterbuton.setOnClickListener {
            var timestamp: Timestamp? = null

            if (dateTextview.text.isNotEmpty() && timeTextview.text.isNotEmpty()) {
                timestamp = Timestamp(cal.time)
            }
            if (selected == "oyuncuBul") {
                criteria = FilterCriteria(
                    category = categorySpinner.selectedItem?.toString(),
                    lookingFor = lookingforSpinner.selectedItem?.toString(),
                    dateTime = timestamp,
                    city = cityTextview.selectedItem?.toString(),
                    townShip = townshipsTextview.selectedItem?.toString()
                )
                getFirebaseData(criteria)
            } else {
                selected = "rakipBul"
                criteria = FilterCriteria(
                    category = categorySpinner.selectedItem?.toString(),
                    dateTime = timestamp,
                    city = cityTextview.selectedItem?.toString(),
                    townShip = townshipsTextview.selectedItem?.toString()
                )
                getFirebaseData(criteria)
            }
            Log.d("deneme", "getFirebaseData: " + criteria)
            if (selected == "rival") {
                fillListRival()
            } else {
                fillList()
            }
        }
    }
}