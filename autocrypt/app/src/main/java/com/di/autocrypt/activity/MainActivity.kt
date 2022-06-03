package com.di.autocrypt.activity

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.di.autocrypt.R
import com.di.autocrypt.model.Repository
import com.di.autocrypt.model.RoomRepository
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , OnMapReadyCallback{

    @Inject
    lateinit var roomRepository: RoomRepository
    val LOCATION_REQUEST_CODE = 100
    lateinit var locationSource: FusedLocationSource
    lateinit var naverMap: NaverMap

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
    }

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map
        locationSource = FusedLocationSource(this,LOCATION_REQUEST_CODE)
        map.locationSource = locationSource
        val uiSettings = map.uiSettings
        uiSettings.isLocationButtonEnabled = true
        showMarker()
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

    fun showMarker(){

        runBlocking {
            val centers = roomRepository.getCenters()
            CoroutineScope(Dispatchers.Main).launch {
                for(center in centers){
                    val marker = Marker()
                    marker.run {
                        position = LatLng(center.lat.toDouble(), center.lng.toDouble())
                        when(center.centerType){
                            "중앙/권역" -> marker.icon = OverlayImage.fromResource(R.drawable.location1)
                            "지역" -> marker.icon = OverlayImage.fromResource(R.drawable.location2)
                            else -> marker.icon = OverlayImage.fromResource(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                        }
                        width = resources.getInteger(R.integer.marker_size)
                        height = resources.getInteger(R.integer.marker_size)
                        isIconPerspectiveEnabled = true
                        anchor = PointF(0.5f,1f)
                        map = naverMap
                    }
                }
            }
        }
    }
}