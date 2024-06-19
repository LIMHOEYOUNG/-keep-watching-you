package com.example.drowsy_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drowsy_pro.databinding.DrowsyrecordPageBinding
import com.example.drowsy_pro.databinding.SleeprrecordRecycleviewBinding
import com.example.drowsy_pro.sleeplog.sleeplistApi
import com.example.drowsy_pro.sleeplog.sleeplogdata
import com.example.drowsy_pro.sleeplog.SleepRecycleadapter
import com.example.drowsy_pro.sleeplog.requestsleep
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class drowsyrecord : AppCompatActivity(),SleepRecycleadapter.ItemClick {
    private lateinit var binding: DrowsyrecordPageBinding
    private lateinit var recyclebinding:SleeprrecordRecycleviewBinding
    private val sleepList = ArrayList<sleeplogdata>()
    private val adapter = SleepRecycleadapter(sleepList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrowsyrecordPageBinding.inflate(layoutInflater)
        recyclebinding=SleeprrecordRecycleviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadDrivingData()
        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupRecyclerView() {
        binding.sleepList.apply {
            layoutManager = LinearLayoutManager(this@drowsyrecord)
            adapter = this@drowsyrecord.adapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@drowsyrecord, LinearLayout.VERTICAL))
        }
        adapter.itemClick = this
    }
    //졸음 기록 클릭시 졸음 맵으로 이동
    override fun onClick(view: View, id: Int, sleepLa: Float,sleepLo:Float,sleepTime: String) {
       val intent = Intent(this, drowsymap::class.java).apply {
            putExtra("ITEM_ID", id)
            putExtra("SLEEP_TIME", sleepTime)
            putExtra("SLEEPLA",sleepLa)
            putExtra("SLEEPLO",sleepLo)
        }
        startActivity(intent)
    }
    //졸음 기록 데이터
    private fun loadDrivingData() {
        val pref = getSharedPreferences("TokenPrefs",MODE_PRIVATE)
        var logintoken=pref.getString("token","Null")
        val requestData = requestsleep(jwt = logintoken?:"Null")
        sleeplistApi.create().getSleepRecords(requestData).enqueue(object :
            Callback<List<sleeplogdata>> {
            override fun onResponse(call: Call<List<sleeplogdata>>, response: Response<List<sleeplogdata>>) {
                if (response.isSuccessful) {
                    sleepList.clear()
                    sleepList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    Log.d("drivingrecord", "Data fetched successfully: ${response.body()}")
                } else {
                    Log.e("drivingrecord", "Failed to fetch data: HTTP ${response.code()} ${response.message()}")
                    Toast.makeText(this@drowsyrecord, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<sleeplogdata>>, t: Throwable) {
                Toast.makeText(this@drowsyrecord, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}