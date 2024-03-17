package com.example.drowsy_pro

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drowsy_pro.databinding.DrivingviewPageBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.overlay.InfoWindow
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class drivingmap : AppCompatActivity() {
    private lateinit var binding: DrivingviewPageBinding
    private var naverMap: NaverMap? = null
    private var polyline: PolylineOverlay? = null
    private var startLocation: String? = null
    private var arriveLocation: String? = null
    private var startTime: String? = null
    private var arrivalTime: String? = null
    private val markerInfoWindows = mutableMapOf<Marker, InfoWindow>()


    data class LocationData(
        val time: String,
        val latitude: Double,
        val longitude: Double
    )

    data class RouteData(
        @SerializedName("date")
        val locations: List<LocationData>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrivingviewPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLocation = intent.getStringExtra("startLo")
        arriveLocation = intent.getStringExtra("arriveLo")
        startTime = intent.getStringExtra("startTime")
        arrivalTime = intent.getStringExtra("arrivalTime")

        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.goMypage.setOnClickListener {
            val intent = Intent(this, mypage::class.java)
            startActivity(intent)
        }
        binding.listButton.setOnClickListener {
            val intent = Intent(this, drivingrecord::class.java)
            startActivity(intent)
        }

        val json = loadJSONFromAsset("testline.json")//test용

        val routeData = Gson().fromJson(json, RouteData::class.java)

        val sortedLocations = routeData.locations.sortedBy {
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                Locale.getDefault()
            ).parse(it.time)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.driving_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.driving_map, it).commit()
            }

        mapFragment.getMapAsync { map ->
            naverMap = map

            // 시작과 끝에 표시
            val startPoint =
                LatLng(sortedLocations.first().latitude, sortedLocations.first().longitude)
            val endPoint = LatLng(sortedLocations.last().latitude, sortedLocations.last().longitude)

            val startMarker = Marker()
            startMarker.position = startPoint
            startMarker.tag = "춥발\n" + startLocation + "\n" + startTime
            startMarker.map = naverMap
            startMarker.setOnClickListener {
                showInfoWindow(startMarker)
                true
            }

            val endMarker = Marker()
            endMarker.position = endPoint
            endMarker.iconTintColor = Color.RED
            endMarker.tag = "도착\n" + arriveLocation + "\n" + arrivalTime
            endMarker.map = naverMap
            endMarker.setOnClickListener {
                showInfoWindow(endMarker) // 마커를 클릭하면 정보 창을 표시
                true
            }

            //카메라 시작 위치
            val bounds = LatLngBounds.Builder().include(startPoint).include(endPoint).build()
            val cameraUpdate = CameraUpdate.fitBounds(bounds, 200)
            naverMap!!.moveCamera(cameraUpdate)

            //라인 그리기
            val points = mutableListOf<LatLng>()
            sortedLocations.forEach {
                points.add(LatLng(it.latitude, it.longitude))
            }
            polyline = PolylineOverlay()
            polyline?.coords = points
            polyline?.color = resources.getColor(android.R.color.holo_blue_dark)
            polyline?.width = 10
            polyline?.map = naverMap
        }

    }

    //test json파일 읽기
    private fun loadJSONFromAsset(fileName: String): String {
        var json: String? = null
        try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

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

