package com.di.autocrypt.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CenterDAO {

    @Query("SELECT * FROM centers")
    suspend fun getAll(): List<Center>

    @Query("SELECT * FROM centers WHERE id = :centerID")
    suspend fun getCenter(centerID: String): List<Center>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(centers: List<Center>)

    @Query("DELETE FROM centers")
    suspend fun delete()
}