package com.example.pharmacymap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pharmacymap.data.local.entity.MedicineEntity

@Dao
interface MedicineDao {

    @Insert
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("SELECT * FROM medicine ORDER BY createdAt DESC")
    suspend fun getAllMedicines(): List<MedicineEntity>
}
