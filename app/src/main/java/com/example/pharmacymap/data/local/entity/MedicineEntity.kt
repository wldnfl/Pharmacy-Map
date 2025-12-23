package com.example.pharmacymap.data.local.entity

import java.io.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val imagePath: String,
    val name: String,
    val purpose: String,
    val startDate: String,
    val memo: String,
    val createdAt: Long
) : Serializable
