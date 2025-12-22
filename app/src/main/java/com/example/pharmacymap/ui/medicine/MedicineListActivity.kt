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

    private val medicineList = mutableListOf<MedicineEntity>()
    private lateinit var adapter: MedicineAdapter
    private lateinit var db: AppDatabase

    companion object {
        const val REQUEST_ADD_MEDICINE = 100
        const val REQUEST_DETAIL = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_list)

        db = AppDatabase.getInstance(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerMedicine)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddMedicine)

        // Adapter 초기화: 클릭 시 상세화면으로 이동
        adapter = MedicineAdapter(medicineList) { medicine ->
            val intent = Intent(this, MedicineDetailActivity::class.java)
            intent.putExtra("medicine", medicine as java.io.Serializable)
            startActivityForResult(intent, REQUEST_DETAIL)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // + 버튼 클릭 시 MedicineAddActivity로 이동
        fab.setOnClickListener {
            val intent = Intent(this, MedicineAddActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_MEDICINE)
        }

        loadMedicines() // 앱 시작 시 DB에서 데이터 로드
    }

    // DB에서 약 목록 가져와 리스트 갱신
    private fun loadMedicines() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.medicineDao().getAllMedicines()
            medicineList.clear()
            medicineList.addAll(list)
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {

            // 삭제 처리
            if (data.getBooleanExtra("deleted", false)) {
                val deletedId = data.getIntExtra("medicineId", -1)
                medicineList.removeAll { it.id == deletedId }
                adapter.notifyDataSetChanged()
                return
            }

            // 수정 / 상세화면에서 돌아왔을 때 리스트 갱신
            if (requestCode == REQUEST_DETAIL) {
                loadMedicines()
                return
            }

            // 새로 추가된 약
            if (requestCode == REQUEST_ADD_MEDICINE) {
                val newMedicine = MedicineEntity(
                    name = data.getStringExtra("name") ?: "",
                    purpose = data.getStringExtra("purpose") ?: "",
                    startDate = data.getStringExtra("startDate") ?: "",
                    memo = data.getStringExtra("memo") ?: "",
                    imagePath = data.getStringExtra("imagePath") ?: "",
                    createdAt = System.currentTimeMillis()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    db.medicineDao().insertMedicine(newMedicine)
                    val updatedList = db.medicineDao().getAllMedicines()
                    runOnUiThread {
                        medicineList.clear()
                        medicineList.addAll(updatedList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
