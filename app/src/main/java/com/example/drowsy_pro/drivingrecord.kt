package com.example.drowsy_pro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drowsy_pro.databinding.DrivingrecordPageBinding
import com.example.drowsy_pro.databinding.DrivingrecordRecyclerviewBinding
import com.example.drowsy_pro.operationrecord.dirvinglistApi
import com.example.drowsy_pro.operationrecord.drivingdata
import com.example.drowsy_pro.operationrecord.requestdriving
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class drivingrecord : AppCompatActivity(),DrivingRecycleadapter.ItemClick {
    private lateinit var binding: DrivingrecordPageBinding
    private lateinit var recyclebinding: DrivingrecordRecyclerviewBinding
    private val drivingList = ArrayList<drivingdata>()
    private val adapter = DrivingRecycleadapter(drivingList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrivingrecordPageBinding.inflate(layoutInflater)
        recyclebinding=DrivingrecordRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadDrivingData()
        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupRecyclerView() {
        binding.drivigList.apply {
            layoutManager = LinearLayoutManager(this@drivingrecord)
            adapter = this@drivingrecord.adapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@drivingrecord, LinearLayout.VERTICAL))
        }
        adapter.itemClick = this
    }
    //리스트 클릭 시 맵으로 이동
    override fun onClick(view: View, id: Int, startTime: String, endTime: String) {
        val intent = Intent(this, drivingmap::class.java).apply {
            putExtra("ITEM_ID", id)
            putExtra("START_TIME", startTime)
            putExtra("END_TIME", endTime)
        }
        startActivity(intent)
    }
    //리스트 보여주기
    private fun loadDrivingData() {
        val pref = getSharedPreferences("TokenPrefs",MODE_PRIVATE)
        var logintoken=pref.getString("token","Null")
        val requestData = requestdriving(jwt = logintoken?:"Null")
        dirvinglistApi.create().getDrivingRecords(requestData).enqueue(object : Callback<List<drivingdata>> {
            override fun onResponse(call: Call<List<drivingdata>>, response: Response<List<drivingdata>>) {
                if (response.isSuccessful) {
                    drivingList.clear()
                    drivingList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    Log.d("drivingrecord", "Data fetched successfully: ${response.body()}")
                } else {
                    Log.e("drivingrecord", "Failed to fetch data: HTTP ${response.code()} ${response.message()}")
                    Toast.makeText(this@drivingrecord, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<drivingdata>>, t: Throwable) {
                Toast.makeText(this@drivingrecord, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}