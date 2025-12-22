package com.example.pharmacymap.ui.medicine

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacymap.R
import com.example.pharmacymap.data.local.entity.MedicineEntity
import java.io.File
import java.io.FileOutputStream

class MedicineAddActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private var imageUri: String = ""
    private var existingMedicine: MedicineEntity? = null

    companion object {
        const val REQUEST_GALLERY = 200
        const val REQUEST_CAMERA = 201
        const val REQUEST_CAMERA_PERMISSION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_add)

        imgPreview = findViewById(R.id.imgPreview)
        val etName = findViewById<EditText>(R.id.etName)
        val etPurpose = findViewById<EditText>(R.id.etPurpose)
        val etStartDate = findViewById<EditText>(R.id.etStartDate)
        val etMemo = findViewById<EditText>(R.id.etMemo)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        val btnCamera = findViewById<Button>(R.id.btnCamera)

        // EditText 클릭 시 DatePickerDialog 열기
        etStartDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            val datePicker = android.app.DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // 선택한 날짜를 EditText에 표시
                    val monthStr = (selectedMonth + 1).toString().padStart(2, '0')
                    val dayStr = selectedDay.toString().padStart(2, '0')
                    etStartDate.setText("$selectedYear-$monthStr-$dayStr")
                }, year, month, day
            )
            datePicker.show()
        }

        // 기존 데이터 불러오기 (수정용)
        existingMedicine = intent.getSerializableExtra("medicine") as? MedicineEntity
        existingMedicine?.let {
            etName.setText(it.name)
            etPurpose.setText(it.purpose)
            etStartDate.setText(it.startDate)
            etMemo.setText(it.memo)
            imageUri = it.imagePath
            if (it.imagePath.isNotEmpty())
                imgPreview.setImageURI(Uri.parse(it.imagePath))
        }

        // 갤러리 선택
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        // 카메라 촬영
        btnCamera.setOnClickListener {
            // 권한 확인
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

        // 저장 버튼
        btnSave.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("name", etName.text.toString())
                putExtra("purpose", etPurpose.text.toString())
                putExtra("startDate", etStartDate.text.toString())
                putExtra("memo", etMemo.text.toString())
                putExtra("imagePath", imageUri)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val uri: Uri? = data.data
                    uri?.let {
                        imageUri = it.toString()
                        imgPreview.setImageURI(it)
                    }
                }

                REQUEST_CAMERA -> {
                    val bitmap = data.extras?.get("data") as Bitmap
                    imgPreview.setImageBitmap(bitmap)

                    // bitmap을 파일로 저장하고 경로를 imageUri에 저장
                    val file = File(cacheDir, "captured_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                    imageUri = file.absolutePath
                }
            }
        }
    }
}
