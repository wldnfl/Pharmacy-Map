package com.example.pharmacymap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacymap.ui.medicine.MedicineListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFindPharmacy = findViewById<Button>(R.id.btnFindPharmacy)
        val btnMyMedicine = findViewById<Button>(R.id.btnMyMedicine) // ✅ 추가

        btnFindPharmacy.setOnClickListener {
            val intent = Intent(this, FindPharmacyActivity::class.java)
            startActivity(intent)
        }

        btnMyMedicine.setOnClickListener {
            val intent = Intent(this, MedicineListActivity::class.java)
            startActivity(intent)
        }
    }
}
