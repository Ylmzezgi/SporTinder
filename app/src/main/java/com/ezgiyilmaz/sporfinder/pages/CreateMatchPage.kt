package com.ezgiyilmaz.sporfinder.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ezgiyilmaz.sporfinder.R
import com.ezgiyilmaz.sporfinder.databinding.ActivityCreateMatchPageBinding
import com.ezgiyilmaz.sporfinder.models.city
import com.ezgiyilmaz.sporfinder.util.constants
import com.ezgiyilmaz.sporfinder.viewModel.CreateMatchViewModel
import com.ezgiyilmaz.sporfinder.viewModel.LocationPickerViewModel
import com.ezgiyilmaz.sporfinder.viewModel.registerPageViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class CreateMatchPage : AppCompatActivity() {
    private lateinit var binding: ActivityCreateMatchPageBinding
    private lateinit var locationPicker: LocationPickerViewModel
    private lateinit var createMatchViewModel: CreateMatchViewModel
    val cal = Calendar.getInstance()
    var selected=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMatchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        showDatePicker()
        showTimePicker()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        locationPicker = ViewModelProvider(this).get(LocationPickerViewModel::class.java)
        createMatchViewModel = ViewModelProvider(this).get(CreateMatchViewModel::class.java)

        locationPicker.getApiInterface()
        GlobalScope.launch(Dispatchers.IO) {
            locationPicker.getCity()
            withContext(Dispatchers.Main) {
                fillSpinner(locationPicker.cities, binding.cityTextView)
                fillSpinner(constants.categories, binding.CategoryTextView)
            }
        }

        binding.radioRival.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selected="rival"
                binding.lookingForTextView.isEnabled = false
            } else {
                selected="player"
                binding.lookingForTextView.isEnabled = true
            }
        }

        binding.CategoryTextView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Seçilen öğe burada işlenebilir
                    val selectedCategory = parent.getItemAtPosition(position).toString()
                    println(selectedCategory)
                    GlobalScope.launch(Dispatchers.IO) {
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
                            fillSpinner(listof, binding.lookingForTextView)
                        }
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }



        binding.cityTextView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                val selectedCity = parent.getItemAtPosition(position).toString()
                println("Seçilen Şehir: $selectedCity")

                val selected =
                    locationPicker.cityId.filter { city: city -> city.name == selectedCity }

                GlobalScope.launch(Dispatchers.IO) {
                    locationPicker.getTownShip(selected.firstOrNull()?.id)
                    withContext(Dispatchers.Main) {
                        fillSpinner(locationPicker.townShips, binding.townShipTextView)
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }


    private fun showDatePicker() {

        binding.dateTextView.setText(SimpleDateFormat("dd/MM/yyyy", Locale.US).format(cal.time))

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                binding.dateTextView.setText(sdf.format(cal.time))
            }

        binding.dateTextView.setOnClickListener {
            val dialog = DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
    }


    private fun showTimePicker() {

        binding.timeTextView.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.timeTextView.setText(timeFormat.format(cal.time))
            }

            TimePickerDialog(
                this, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true
            ).show() // 24 saat formatı için true parametresi
        }
    }

    suspend fun fillSpinner(list: List<String>, spinner: Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun ClickCreateMatch(view: View) {
        val radioPlayer = binding.radioPlayer
        val radioRival = binding.radioRival
        val Category = binding.CategoryTextView.selectedItem.toString()
        val lookingFor = binding.lookingForTextView.selectedItem.toString()
        val date = binding.dateTextView.text.toString()
        val time = binding.timeTextView.text.toString()
        val city = binding.cityTextView.selectedItem.toString()
        val townShip = binding.townShipTextView.selectedItem.toString()
        val note = binding.noteEditText.text.toString()



        GlobalScope.launch(Dispatchers.IO) {
            val result = createMatchViewModel.saveViewModel(
                selected,
                Category,
                lookingFor,
                date,
                time,
                Timestamp(cal.time),
                city,
                townShip,
                note,
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@CreateMatchPage, result, Toast.LENGTH_LONG).show()
            }
        }

    }

}
