package com.example.pharmacymap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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
