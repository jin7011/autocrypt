package com.di.autocrypt.model

import com.di.autocrypt.data.CenterResult
import javax.inject.Inject

class Repository @Inject constructor(private val service: Service){

    suspend fun getCenters(page:Int, perPage:Int):CenterResult{
        return service.getCenters(page, perPage)
    }
}