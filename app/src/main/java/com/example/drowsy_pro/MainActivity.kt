package com.example.drowsy_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drowsy_pro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goMypage.setOnClickListener {
            val intent = Intent(this, mypage::class.java)
            startActivity(intent)
        }
        binding.goLocationcar.setOnClickListener {
            val intent = Intent(this, locationcar::class.java)
            startActivity(intent)
        }
        binding.goDrowsynotification.setOnClickListener {
            val intent = Intent(this, drowsynotification::class.java)
            startActivity(intent)
        }
        binding.goUnperson.setOnClickListener {
            val intent = Intent(this, unperson::class.java)
            startActivity(intent)
        }
        binding.goDrivingrecord.setOnClickListener {
            val intent = Intent(this, drivingrecord::class.java)
            startActivity(intent)
        }
        binding.goAccident.setOnClickListener {
            val intent = Intent(this, accidentrecord::class.java)
            startActivity(intent)
        }
        binding.goDrowsyrecord.setOnClickListener {
            val intent = Intent(this, drowsyrecord::class.java)
            startActivity(intent)
        }
    }
}
