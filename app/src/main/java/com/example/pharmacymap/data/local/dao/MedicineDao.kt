package com.example.pharmacymap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pharmacymap.data.local.entity.MedicineEntity

@Dao
interface MedicineDao {

    @Insert
    suspend fun insertMedicine(medicine: MedicineEntity)

    @Query("SELECT * FROM medicine ORDER BY createdAt DESC")
    suspend fun getAllMedicines(): List<MedicineEntity>
}