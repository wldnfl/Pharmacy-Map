package com.example.pharmacymap

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.widget.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFindPharmacy = findViewById<Button>(R.id.btnFindPharmacy)

        btnFindPharmacy.setOnClickListener {
            val intent = Intent(this, FindPharmacyActivity::class.java)
            startActivity(intent)
        }
    }
}
