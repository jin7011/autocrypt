package com.di.autocrypt.model

import com.di.autocrypt.data.CenterResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface Service {
    @GET("centers")
    suspend fun getCenters(
        @Query("page") page:Int = 1,
        @Query("perPage") perPage:Int = 10,
        @Query("serviceKey") serviceKey: String = authKey
    ):CenterResult

    companion object{
        private const val URL = "https://api.odcloud.kr/api/15077586/v1/"
        private const val authKey = "bNmSjmL3NWL/mAmsQV0SyDT+8DCdZckhVg5/tSsmJHa47eBZBE+aFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg=="

        fun create(): Service {
            val httpClient = OkHttpClient.Builder()
                .callTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)

            return Retrofit.Builder()
                .baseUrl(URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service::class.java)
        }
    }
}