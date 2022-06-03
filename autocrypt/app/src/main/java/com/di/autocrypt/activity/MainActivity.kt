package com.di.autocrypt.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.di.autocrypt.R
import com.di.autocrypt.model.Repository
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , OnMapReadyCallback{

    @Inject
    lateinit var repository: Repository
    val LOCATION_REQUEST_CODE = 100
    lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("d0g2va8emc")

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map,fm.findFragmentById(R.id.map)!!).commit()
            }

        mapFragment.getMapAsync(this)

        CoroutineScope(Dispatchers.Main).launch {
            Log.e("Asd","${repository.getCenters(1,10)}")
        }

    }

    override fun onMapReady(map: NaverMap) {
        locationSource = FusedLocationSource(this,LOCATION_REQUEST_CODE)
        map.locationSource = locationSource
        val uiSettings = map.uiSettings
        uiSettings.isLocationButtonEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            LOCATION_REQUEST_CODE -> {
                locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)
            }
        }
    }
}