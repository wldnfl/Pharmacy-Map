package com.example.pharmacymap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pharmacymap.data.local.dao.MedicineDao
import com.example.pharmacymap.data.local.entity.MedicineEntity

@Database(
    entities = [MedicineEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicine.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
