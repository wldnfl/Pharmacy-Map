package com.example.pharmacymap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine")
data class MedicineEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val imagePath: String,      // 사진 파일 경로
    val name: String,           // 약 이름
    val purpose: String,        // 복용 목적
    val startDate: String,      // 복용 시작일
    val memo: String,           // 메모
    val createdAt: Long         // 등록 시간
)