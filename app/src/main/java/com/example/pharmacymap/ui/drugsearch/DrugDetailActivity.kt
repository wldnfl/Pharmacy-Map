package com.example.pharmacymap.ui.drugsearch

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacymap.R
import com.example.pharmacymap.data.local.AppDatabase
import com.example.pharmacymap.data.local.entity.MedicineEntity
import com.example.pharmacymap.data.remote.DrugItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrugDetailActivity : AppCompatActivity() {

    private lateinit var drug: DrugItem
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drug_detail)

        db = AppDatabase.getInstance(this)
        drug = intent.getSerializableExtra("drug") as DrugItem

        val tvName = findViewById<TextView>(R.id.tvDrugName)
        val tvEfcy = findViewById<TextView>(R.id.tvDrugEfcy)
        val tvUse = findViewById<TextView>(R.id.tvDrugUse)
        val tvAtpn = findViewById<TextView>(R.id.tvDrugAtpn)
        val tvSe = findViewById<TextView>(R.id.tvDrugSe)
        val tvDeposit = findViewById<TextView>(R.id.tvDrugDeposit)
        val btnAdd = findViewById<Button>(R.id.btnAddToMyMedicine)

        tvName.text = drug.itemName
        tvEfcy.text = "효능: ${drug.efcyQesitm ?: "-"}"
        tvUse.text = "사용법: ${drug.useMethodQesitm ?: "-"}"
        tvAtpn.text = "주의사항: ${drug.atpnQesitm ?: "-"}"
        tvSe.text = "부작용: ${drug.seQesitm ?: "-"}"
        tvDeposit.text = "보관법: ${drug.depositMethodQesitm ?: "-"}"

        btnAdd.setOnClickListener {
            val medicine = MedicineEntity(
                name = drug.itemName ?: "",
                purpose = drug.efcyQesitm ?: "",
                startDate = "",
                memo = "",
                imagePath = "",
                createdAt = System.currentTimeMillis()
            )
            CoroutineScope(Dispatchers.IO).launch {
                db.medicineDao().insertMedicine(medicine)
                finish()
            }
        }
    }
}
