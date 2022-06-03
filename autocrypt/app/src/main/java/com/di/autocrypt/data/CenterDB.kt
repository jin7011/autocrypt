package com.di.autocrypt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Center::class], version = 2, exportSchema = false)
abstract class CenterDB: RoomDatabase() {
    abstract fun centerDAO(): CenterDAO

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: CenterDB? = null

        fun getInstance(context: Context): CenterDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): CenterDB {
            return Room.databaseBuilder(context, CenterDB::class.java, "DB")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}