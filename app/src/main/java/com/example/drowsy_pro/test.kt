package com.example.drowsy_pro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drowsy_pro.databinding.TestviewBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class test : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: TestviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TestviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.test_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.test_map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        val coordinateString = "3742.99145 N 12632.36238 E"
        val coordinate = parseCoordinate(coordinateString)
        val marker = Marker()
        marker.position = coordinate
        marker.map = naverMap
    }

    private fun parseCoordinate(coordinateString: String): LatLng {
        val parts = coordinateString.split(" ")
        val latitude = parts[0].substring(0, 2).toDouble() + parts[0].substring(2).toDouble() / 60
        val longitude = parts[2].substring(0, 3).toDouble() + parts[2].substring(3).toDouble() / 60
        return if (parts[1] == "S") LatLng(-latitude, longitude) else LatLng(latitude, longitude)
    }
}
