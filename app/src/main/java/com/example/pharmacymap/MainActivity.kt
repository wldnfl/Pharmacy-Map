package com.example.pharmacymap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacymap.ui.drugsearch.DrugSearchActivity
import com.example.pharmacymap.ui.medicine.MedicineListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFindPharmacy = findViewById<Button>(R.id.btnFindPharmacy)
        val btnMyMedicine = findViewById<Button>(R.id.btnMyMedicine)
        val btnSearchMedicine = findViewById<Button>(R.id.btnSearchMedicine)

        btnFindPharmacy.setOnClickListener {
            val intent = Intent(this, FindPharmacyActivity::class.java)
            startActivity(intent)
        }

        btnMyMedicine.setOnClickListener {
            val intent = Intent(this, MedicineListActivity::class.java)
            startActivity(intent)
        }

        btnSearchMedicine.setOnClickListener {
            val intent = Intent(this, DrugSearchActivity::class.java)
            startActivity(intent)
        }
    }
}
