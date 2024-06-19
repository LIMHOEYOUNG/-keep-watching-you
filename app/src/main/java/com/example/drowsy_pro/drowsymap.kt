package com.example.drowsy_pro

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drowsy_pro.databinding.DrowsymapPageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.OverlayImage

class drowsymap : AppCompatActivity() {

    private lateinit var binding: DrowsymapPageBinding
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var SleepTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrowsymapPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SleepTime = intent.getStringExtra("SLEEP_TIME")
        val Sleepla = intent.getFloatExtra("SLEEPLA", 0F)
        val Sleeplo = intent.getFloatExtra("SLEEPLO", 0F)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 지도 초기화
        try {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.drowsy_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().add(R.id.drowsy_map, it).commit()
                }

            // map 로드 후 호출
            mapFragment.getMapAsync { map ->
                naverMap = map
                Log.d("showmap", "7")

                // 지도 초기 위치 설정
                val initialPosition = LatLng(Sleeplo.toDouble(), Sleepla.toDouble())
                val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
                naverMap.moveCamera(cameraUpdate)

                // 마커 설정
                val currentCarMarker = Marker()
                currentCarMarker.position = initialPosition
                currentCarMarker.tag = "졸음 시간: ${SleepTime}"
                currentCarMarker.map = naverMap
                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return currentCarMarker.tag as? String ?: ""
                    }
                }
                infoWindow.open(currentCarMarker)
            }
        } catch (e: Exception) {
            Log.e("showmap", "Error initializing MapFragment", e)
        }

        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.listButton.setOnClickListener {
            val intent = Intent(this, drowsyrecord::class.java)
            startActivity(intent)
        }
    }
}
