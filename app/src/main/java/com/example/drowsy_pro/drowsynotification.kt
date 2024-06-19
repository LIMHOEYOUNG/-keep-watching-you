package com.example.drowsy_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drowsy_pro.databinding.DrowsynotificationPageBinding
import android.util.Log

class drowsynotification : AppCompatActivity() {
    private lateinit var binding: DrowsynotificationPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrowsynotificationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("drowsylog", "drowsynotification activity created")
        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.stopAlarm.setOnClickListener {
            stopAlarmService()
        }
    }
    private fun stopAlarmService() {
        val serviceIntent = Intent(this, AlarmService::class.java)
        stopService(serviceIntent)
    }
}