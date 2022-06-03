package com.di.autocrypt.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.di.autocrypt.R
import com.di.autocrypt.data.Center
import com.di.autocrypt.databinding.ActivityMainBinding
import com.di.autocrypt.model.RoomRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.rx3.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
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
    lateinit var navercInfoWindow:InfoWindow
    lateinit var naverMap: NaverMap
    lateinit var binding:ActivityMainBinding
    private var locationManager: LocationManager? = null
    val markerList = ArrayList<Marker>() //당장안쓰지만 이후에 쓰일 가능성 높음.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView()
    }

    fun setView(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        //확인 누르면 정보창 닫힘
        binding.btnConfirm.setOnClickListener {
            binding.infoLinear.visibility = View.GONE
        }

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
//        val uiSettings = map.uiSettings 기존 내 위치찾기 설정
//        uiSettings.isLocationButtonEnabled = true
        navercInfoWindow = InfoWindow()
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
                            camMove(currentLocation!!.latitude, currentLocation!!.longitude)
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

    @SuppressLint("PrivateResource")
    fun showMarker(){
        runBlocking(Dispatchers.IO){
            val centers = roomRepository.getCenters()
            CoroutineScope(Dispatchers.Main).launch {
                for(center in centers){
                    val marker = Marker()
                    marker.run {
                        tag = center
                        position = LatLng(center.lat.toDouble(), center.lng.toDouble())
                        when(center.centerType){
                            "중앙/권역" -> marker.icon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_default_marker_icon_yellow)
                            "지역" -> marker.icon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_default_marker_icon_green)
                            else -> marker.icon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_default_marker_icon_black)
                        }
//                        width = resources.getInteger(R.integer.marker_size) 마커 크기조절
//                        height = resources.getInteger(R.integer.marker_size)
                        isIconPerspectiveEnabled = true //마커 원근감
                        anchor = PointF(0.5f,1f) //마커의 위치정도

                        //정보창 클릭이벤트
                        setOnClickListener{
                            navercInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this@MainActivity){
                                override fun getText(info: InfoWindow): CharSequence {
                                    val center = info.marker?.tag as Center
                                    camMove(center.lat.toDouble(),center.lng.toDouble())
                                    binding.center = center
                                    return center.facilityName
                                }
                            }

                            // 이미 현재 마커에 정보 창이 열려있을 경우 닫음, 다른 거 누르면 꺼지고 새로운거 켜짐(정보창 포함)
                            if(navercInfoWindow.marker != null){
                                if(navercInfoWindow.marker != marker) {
                                    navercInfoWindow.close()
                                    navercInfoWindow.open(marker)
                                    binding.infoLinear.visibility = View.VISIBLE
                                }else {
                                    navercInfoWindow.close()
                                    binding.infoLinear.visibility = View.GONE
                                }
                            }else {
                                navercInfoWindow.open(marker)
                                binding.infoLinear.visibility = View.VISIBLE
                            }
                            return@setOnClickListener false
                        }
                        map = naverMap
                    }
                    markerList.add(marker)
                }
            }
        }
    }

    //카메라 이동
    fun camMove(latitude:Double, longitude:Double){
        val cameraUpdate = CameraUpdate.scrollTo(
            LatLng(
                latitude,
                longitude
            )
        )
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 100
        private const val TAG = "Main"
    }
}