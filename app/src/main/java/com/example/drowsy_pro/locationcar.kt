package com.example.drowsy_pro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.drowsy_pro.currentcar.carlocationApi
import com.example.drowsy_pro.currentcar.currentdata
import com.example.drowsy_pro.currentcar.currentjwt
import com.example.drowsy_pro.databinding.LocationcarPageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class locationcar : AppCompatActivity() {

    private lateinit var binding: LocationcarPageBinding
    private lateinit var naverMap: NaverMap
    private var currentCarLatLng: LatLng =LatLng(37.5663, 126.9779)
    private var currentUserLatLng: LatLng? = null // 현재 사용자의 위치
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationcarPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //지도 초기화
        val mapFragment = supportFragmentManager.findFragmentById(R.id.current_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.current_map, it).commit()
            }

        // 지도 로드 후 호출
        mapFragment.getMapAsync { map ->
            naverMap = map
            carposition()
        }

        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.restartButton.setOnClickListener {
            requestLocationPermission()
        }
    }

    // 위치 권한 요청
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    // 현재 위치 확인
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 없으면 요청
            requestLocationPermission()
            return
        }
        // 현재 위치 확인 후 지도에 표시
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentUserLatLng = LatLng(it.latitude, it.longitude)

                // 현재 위치를 지도에 표시
                currentUserLatLng?.let { userLatLng ->
                    val userMarker = Marker()
                    userMarker.position = userLatLng
                    userMarker.icon = OverlayImage.fromResource(R.drawable.herepeople_icon)
                    userMarker.map = naverMap

                    val bounds = LatLngBounds.Builder().include(currentCarLatLng).include(userLatLng).build()
                    val cameraUpdate = CameraUpdate.fitBounds(bounds, 200)
                    naverMap.moveCamera(cameraUpdate)
                }
            }
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate()
        } else {
            // 권한 거부 처리
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "설정에서 위치 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 현재 자동차 위치 불러오기
    private fun carposition() {
        val pref = getSharedPreferences("TokenPrefs", MODE_PRIVATE)
        val logintoken = pref.getString("token", "Null")
        val requestData = currentjwt(jwt = logintoken ?: "Null")
        carlocationApi.create().carlocation(requestData).enqueue(object : Callback<currentdata> {
            override fun onResponse(call: Call<currentdata>, response: Response<currentdata>) {
                if (response.isSuccessful) {
                    val carlatitude = response.body()?.latitude
                    val carlongitude = response.body()?.longitude
                    if (carlatitude != null && carlongitude != null) {
                        currentCarLatLng = LatLng(carlatitude.toDouble(), carlongitude.toDouble())
                        Log.d("carlocation fun", "${currentCarLatLng}")

                        // 마커 설정
                        val currentCarMarker = Marker()
                        currentCarMarker.position = currentCarLatLng
                        currentCarMarker.icon = OverlayImage.fromResource(R.drawable.herecar_icon)
                        currentCarMarker.map = naverMap

                        getCurrentLocation()
                    } else {
                        Log.e("LocationError", "Latitude or Longitude is null")
                    }
                } else {
                    Toast.makeText(this@locationcar, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<currentdata>, t: Throwable) {
                Toast.makeText(this@locationcar, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
