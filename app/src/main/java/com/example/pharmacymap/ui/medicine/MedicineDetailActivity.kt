package com.example.pharmacymap.ui.medicine

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pharmacymap.R
import com.example.pharmacymap.data.local.AppDatabase
import com.example.pharmacymap.data.local.entity.MedicineEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MedicineDetailActivity : AppCompatActivity() {

    private lateinit var medicine: MedicineEntity
    private lateinit var db: AppDatabase

    private lateinit var tvName: TextView
    private lateinit var tvPurpose: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvMemo: TextView
    private lateinit var img: ImageView

    companion object {
        const val REQUEST_EDIT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_detail)

        db = AppDatabase.getInstance(this)
        medicine = intent.getSerializableExtra("medicine") as? MedicineEntity ?: run {
            finish()
            return
        }

        img = findViewById(R.id.imgMedicine)
        tvName = findViewById(R.id.tvName)
        tvPurpose = findViewById(R.id.tvPurpose)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvMemo = findViewById(R.id.tvMemo)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        displayMedicine()

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        db.medicineDao().deleteMedicine(medicine)
                        runOnUiThread {
                            val resultIntent = Intent().apply {
                                putExtra("deleted", true)
                                putExtra("medicineId", medicine.id)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, MedicineAddActivity::class.java)
            intent.putExtra("medicine", medicine)
            startActivityForResult(intent, REQUEST_EDIT)
        }
    }

    private fun displayMedicine() {
        tvName.text = medicine.name
        tvPurpose.text = "목적: ${medicine.purpose}"
        tvStartDate.text = "복용 시작일: ${medicine.startDate}"
        tvMemo.text = "메모: ${medicine.memo}"
        if (medicine.imagePath.isNotEmpty())
            Glide.with(this).load(File(medicine.imagePath)).into(img)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MedicineAddActivity.REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) return

        if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK && data != null) {
            val updatedMedicine = data.getSerializableExtra("medicine") as? MedicineEntity ?: return
            medicine = updatedMedicine
            displayMedicine()

            val resultIntent = Intent().apply {
                putExtra("medicine", updatedMedicine)
            }
            setResult(Activity.RESULT_OK, resultIntent)
        }
    }
}
