package com.example.drowsy_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drowsy_pro.databinding.DrivingrecordPageBinding
import com.example.drowsy_pro.databinding.DrivingrecordRecyclerviewBinding



class drivingrecord : AppCompatActivity() {
    private lateinit var binding: DrivingrecordPageBinding
    private lateinit var recyclebinding:DrivingrecordRecyclerviewBinding
    private val Drivinglist = ArrayList<Drivingrecycledata>()
    private val adapter = DrivingRecycleadapter(Drivinglist)
    private var start_location="test 출발 장소" //임시
    private var arrival_location="test 도착 장소" //임시
    private var start_time="2024-03-16 15:12:14" //임시
    private var arrival_time="2024-03-16 15:32:14" //임시


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrivingrecordPageBinding.inflate(layoutInflater)
        recyclebinding=DrivingrecordRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.goMypage.setOnClickListener {
            val intent = Intent(this, mypage::class.java)
            startActivity(intent)
        }
        binding.drivigList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =   DrivingRecycleadapter(Drivinglist)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }
        Drivinglist.add(Drivingrecycledata(start_location, arrival_location,start_time,arrival_time))//임시
        Drivinglist.add(Drivingrecycledata("test 출발 장소 2", "test 도착 장소 2",start_time,arrival_time))//임시

        binding.drivigList.adapter = adapter
        binding.drivigList.layoutManager = LinearLayoutManager(this)

        adapter.itemClick = object : DrivingRecycleadapter.ItemClick { //임시
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@drivingrecord, drivingmap::class.java)
                intent.putExtra("startLo", start_location)
                intent.putExtra("arriveLo", arrival_location)
                intent.putExtra("startTime", start_time)
                intent.putExtra("arrivalTime", arrival_time)
                startActivity(intent)
            }
        }
    }

}