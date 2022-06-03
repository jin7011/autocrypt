package com.di.autocrypt.model

import com.di.autocrypt.data.Center
import com.di.autocrypt.data.CenterDAO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository  @Inject constructor(private val centerDAO: CenterDAO){

    fun getCenters() = centerDAO.getAll()

    fun insertCenters(centers:List<Center>) = centerDAO.insertAll(centers)

    fun getCenter(centerID:String) = centerDAO.getCenter(centerID)

    fun deleteAll() = centerDAO.delete()
}