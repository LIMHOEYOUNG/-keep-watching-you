package com.example.drowsy_pro

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.drowsy_pro.databinding.DrivingviewPageBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.overlay.InfoWindow
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.drowsy_pro.operationrecord.drivingmapApi
import com.example.drowsy_pro.operationrecord.requestdrivingmap
import com.example.drowsy_pro.operationrecord.RouteData

class drivingmap : AppCompatActivity() {
    private lateinit var binding: DrivingviewPageBinding
    private var naverMap: NaverMap? = null
    private var polyline: PolylineOverlay? = null
    private var startTime: String? = null
    private var arrivalTime: String? = null
    private var mapid: Int? = null
    private val markerInfoWindows = mutableMapOf<Marker, InfoWindow>()
    private val mapapi = drivingmapApi.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrivingviewPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startTime = intent.getStringExtra("START_TIME")
        arrivalTime = intent.getStringExtra("END_TIME")
        mapid=intent.getIntExtra("ITEM_ID",0)

        binding.goHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.listButton.setOnClickListener {
            startActivity(Intent(this, drivingrecord::class.java))
        }
        initMapFragment()
    }

    //서버에서 데이터 운행기록 가져오기
    private fun fetchDataFromServer() {
        val pref = getSharedPreferences("TokenPrefs", MODE_PRIVATE)
        val logintoken = pref.getString("token", "Null")
        val requestData = requestdrivingmap(jwt = logintoken ?: "Null", id = mapid ?: 0)

        mapapi.getDrivingmap(requestData).enqueue(object : Callback<RouteData> {
            override fun onResponse(call: Call<RouteData>, response: Response<RouteData>) {
                if (response.isSuccessful) {
                    response.body()?.let { routeData ->
                        updateMap(routeData)
                    }
                } else {
                    Log.e("API Error", "Response not successful")
                }
            }

            override fun onFailure(call: Call<RouteData>, t: Throwable) {
                Log.e("Network Error", t.message ?: "Unknown error")
            }
        })
    }
    // 맵 프래그먼트 초기화
    private fun initMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.driving_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.driving_map, it).commit()
            }

        mapFragment.getMapAsync { map ->
            naverMap = map
            fetchDataFromServer()
        }
    }
    // 경로로 맵 업데이트
    private fun updateMap(routeData: RouteData) {
        val sortedLocations = routeData.date.sortedBy {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it.time)
        }
        if (sortedLocations.isNotEmpty()) {
            val points = sortedLocations.map { LatLng(it.latitude, it.longitude) }
            addMarkersAndPolyline(points)
        }
    }
    //마커 폴리라인 추가
    private fun addMarkersAndPolyline(points: List<LatLng>) {
        val startPoint = points.first()
        val endPoint = points.last()

        addMarker(points.first(), true, startTime?:"0")
        addMarker(points.last(), false, arrivalTime?:"0")

        //카메라 시작 위치 설정
        val bounds = LatLngBounds.Builder().include(startPoint).include(endPoint).build()
        naverMap?.moveCamera(CameraUpdate.fitBounds(bounds, 200))

        //라인 그리기
        polyline = PolylineOverlay().apply {
            coords = points
            color = resources.getColor(android.R.color.holo_blue_dark)
            width = 10
            map = naverMap
        }
    }

    //마커 추가
    private fun addMarker(location: LatLng, isStart: Boolean, time: String) {
        val tag = if (isStart) "시작지점\n $time" else "종료지점\n $time"
        Marker().apply {
            position = location
            this.tag=tag
            iconTintColor = if (isStart) Color.GREEN else Color.RED
            map = naverMap
            setOnClickListener {
                showInfoWindow(this)
                true}
        }

    }
    // 마커 정보 추가
    private fun showInfoWindow(marker: Marker) {
        val existingInfoWindow = markerInfoWindows[marker]

        if (existingInfoWindow != null && existingInfoWindow.isVisible) {
            existingInfoWindow.close()
            markerInfoWindows.remove(marker)
        } else {
            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return marker.tag as CharSequence
                }
            }
            infoWindow.open(marker)
            markerInfoWindows[marker] = infoWindow
        }
    }
}
