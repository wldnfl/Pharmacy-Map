package com.example.pharmacymap.ui.medicine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacymap.R
import com.example.pharmacymap.data.local.AppDatabase
import com.example.pharmacymap.data.local.entity.MedicineEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicineListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicineAdapter
    private lateinit var db: AppDatabase
    private val medicineList = mutableListOf<MedicineEntity>()

    companion object {
        const val REQUEST_ADD = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_list)

        db = AppDatabase.getInstance(this)

        recyclerView = findViewById(R.id.recyclerMedicine)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MedicineAdapter(medicineList) { medicine ->
            val intent = Intent(this, MedicineDetailActivity::class.java)
            intent.putExtra("medicine", medicine)
            startActivityForResult(intent, REQUEST_ADD)
        }
        recyclerView.adapter = adapter

        val fabAdd: FloatingActionButton = findViewById(R.id.fabAddMedicine)
        fabAdd.setOnClickListener {
            val intent = Intent(this, MedicineAddActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD)
        }

        loadMedicines()
    }

    private fun loadMedicines() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.medicineDao().getAllMedicines()
            runOnUiThread {
                medicineList.clear()
                medicineList.addAll(list)
                adapter.setItems(medicineList)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            loadMedicines()
        }
    }
}
