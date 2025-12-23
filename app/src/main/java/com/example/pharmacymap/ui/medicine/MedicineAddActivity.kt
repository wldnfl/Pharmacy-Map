package com.example.pharmacymap.ui.medicine

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pharmacymap.R
import com.example.pharmacymap.data.local.AppDatabase
import com.example.pharmacymap.data.local.entity.MedicineEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class MedicineAddActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private var imageUri: String = ""
    private var existingMedicine: MedicineEntity? = null
    private lateinit var db: AppDatabase

    companion object {
        const val REQUEST_GALLERY = 200
        const val REQUEST_CAMERA = 201
        const val REQUEST_CAMERA_PERMISSION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_add)

        db = AppDatabase.getInstance(this)

        imgPreview = findViewById(R.id.imgPreview)
        val etName = findViewById<EditText>(R.id.etName)
        val etPurpose = findViewById<EditText>(R.id.etPurpose)
        val etStartDate = findViewById<EditText>(R.id.etStartDate)
        val etMemo = findViewById<EditText>(R.id.etMemo)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        val btnCamera = findViewById<Button>(R.id.btnCamera)

        // 날짜 입력 EditText 직접 입력 막기
        etStartDate.apply {
            isFocusable = false
            isClickable = true
            keyListener = null
        }

        // DatePicker
        etStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etStartDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 기존 데이터 불러오기 (수정용)
        existingMedicine = intent.getSerializableExtra("medicine") as? MedicineEntity
        existingMedicine?.let {
            etName.setText(it.name)
            etPurpose.setText(it.purpose)
            etStartDate.setText(it.startDate)
            etMemo.setText(it.memo)
            imageUri = it.imagePath
            if (it.imagePath.isNotEmpty()) {
                Glide.with(this)
                    .load(File(it.imagePath))
                    .fitCenter()
                    .into(imgPreview)
            }
        }

        // 갤러리 선택
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        // 카메라 촬영
        btnCamera.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }

        // 저장
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val purpose = etPurpose.text.toString()
            val startDate = etStartDate.text.toString()
            val memo = etMemo.text.toString()
            val createdAt = existingMedicine?.createdAt ?: System.currentTimeMillis()

            val medicine = MedicineEntity(
                id = existingMedicine?.id ?: 0,
                name = name,
                purpose = purpose,
                startDate = startDate,
                memo = memo,
                imagePath = imageUri,
                createdAt = createdAt
            )

            CoroutineScope(Dispatchers.IO).launch {
                if (existingMedicine == null) {
                    val newId = db.medicineDao().insertMedicine(medicine)
                    medicine.id = newId.toInt()
                } else {
                    db.medicineDao().updateMedicine(medicine)
                }

                // 수정된 MedicineEntity 바로 전달
                runOnUiThread {
                    val resultIntent = Intent().apply {
                        putExtra("medicine", medicine) // Serializable
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

    }

    // 권한 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    // 갤러리/카메라 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            REQUEST_GALLERY -> data.data?.let { uri ->
                // content:// -> 실제 파일로 변환
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(cacheDir, "selected_${System.currentTimeMillis()}.jpg")
                inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        input?.copyTo(output)
                    }
                }
                imageUri = file.absolutePath
                Glide.with(this).load(file).into(imgPreview)
            }

            REQUEST_CAMERA -> {
                val bitmap = data.extras?.get("data") as? Bitmap ?: return
                val file = File(cacheDir, "captured_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                imageUri = file.absolutePath
                Glide.with(this).load(file).into(imgPreview)
            }
        }
    }
}
