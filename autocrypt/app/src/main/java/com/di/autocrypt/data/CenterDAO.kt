package com.di.autocrypt.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CenterDAO {

    @Query("SELECT * FROM centers")
    fun getAll(): List<Center>

    @Query("SELECT * FROM centers WHERE id = :centerID")
    fun getCenter(centerID: String): Flow<Center>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(centers: List<Center>)

    @Query("DELETE FROM centers")
    fun delete()
}