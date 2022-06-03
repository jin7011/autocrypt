package com.di.autocrypt.model

import com.di.autocrypt.data.Center
import com.di.autocrypt.data.CenterDAO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository  @Inject constructor(private val centerDAO: CenterDAO){

    suspend fun getCenters() = centerDAO.getAll()

    suspend fun insertCenters(centers:List<Center>) = centerDAO.insertAll(centers)

    suspend fun getCenter(centerID:String) = centerDAO.getCenter(centerID)

    suspend fun deleteAll() = centerDAO.delete()
}