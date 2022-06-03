package com.di.autocrypt.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import com.di.autocrypt.R
import com.di.autocrypt.databinding.ActivityMainBinding
import com.di.autocrypt.model.Repository
import com.di.autocrypt.model.RoomRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.rx3.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
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
class MainActivity : AppCompatActivity() , OnMapReadyCallback, LocationListener{

    @Inject
    lateinit var roomRepository: RoomRepository
    lateinit var locationSource: FusedLocationSource
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var naverMap: NaverMap
    lateinit var binding:ActivityMainBinding
    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("d0g2va8emc")

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map,fm.findFragmentById(R.id.map)!!).commit()
            }

        mapFragment.getMapAsync(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
    }

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermissions()
        map.locationSource = locationSource
//        val uiSettings = map.uiSettings 기존 내 위치찾기 설정해제
//        uiSettings.isLocationButtonEnabled = true
        showMarker() //지도에 마크표시

        //내 위치로 카메라 이동
        binding.fabTracking.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.let {
                    var currentLocation: Location?

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            currentLocation = location
                            naverMap.locationOverlay.run {
                                isVisible = true
                                position =
                                    LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                            }
                            // 카메라 현재위치로 이동
                            val cameraUpdate = CameraUpdate.scrollTo(
                                LatLng(
                                    currentLocation!!.latitude,
                                    currentLocation!!.longitude
                                )
                            )
                            naverMap.moveCamera(cameraUpdate)
                        }
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        naverMap.let {
            val coord = LatLng(location)

            val locationOverlay = it.locationOverlay
            locationOverlay.isVisible = true
            locationOverlay.position = coord
            locationOverlay.bearing = location.bearing

            it.moveCamera(CameraUpdate.scrollTo(coord))
        }
    }

    // 위치권한 관련 요청 tedpermission 라이브러리
    private fun requestPermissions() {
        // 내장 위치 추적 기능 사용
        locationSource =
            FusedLocationSource(this, LOCATION_REQUEST_CODE)
        TedPermission.create()
            .setRationaleTitle("위치권한 요청")
            .setRationaleMessage("현재 위치로 이동하기 위해 위치권한이 필요합니다.") // "we need permission for read contact and find your location"
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .request()
            .subscribe({ tedPermissionResult ->
                if (!tedPermissionResult.isGranted) {
                    Toast.makeText(this,"거절되었습니다.",Toast.LENGTH_SHORT).show()
                }
            }) { throwable -> Log.e("AAAAAA", throwable.message.toString()) }
    }

    fun showMarker(){
        runBlocking(Dispatchers.IO){
            val centers = roomRepository.getCenters()
            CoroutineScope(Dispatchers.Main).launch {
                for(center in centers){
                    val marker = Marker()
                    marker.run {
                        position = LatLng(center.lat.toDouble(), center.lng.toDouble())
                        when(center.centerType){
                            "중앙/권역" -> marker.icon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_default_marker_icon_lightblue)
                            "지역" -> marker.icon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_default_marker_icon_red)
                            else -> marker.icon = OverlayImage.fromResource(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                        }
//                        width = resources.getInteger(R.integer.marker_size)
//                        height = resources.getInteger(R.integer.marker_size)
                        isIconPerspectiveEnabled = true
                        anchor = PointF(0.5f,1f)
                        map = naverMap
                    }
                }
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when(requestCode){
//            LOCATION_REQUEST_CODE -> {
//                locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    locationManager?.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER, 1000, 10f, this)
//                }
//            }
//        }
//    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 100
    }
}