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
import com.example.drowsy_pro.databinding.UnpersonrecordPageBinding
import com.example.drowsy_pro.databinding.UnpersonrecordRecycleviewBinding
import com.example.drowsy_pro.unpersondata.putunpersonlist
import com.example.drowsy_pro.unpersondata.unpersondata
import com.example.drowsy_pro.unpersonlog.UnpersonRecycleadapter
import com.example.drowsy_pro.unpersonlog.unpersonlistApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class unpersonrecord : AppCompatActivity(),UnpersonRecycleadapter.ItemClick {
    private lateinit var binding: UnpersonrecordPageBinding
    private lateinit var recyclebinding:UnpersonrecordRecycleviewBinding
    private val UnpersonList = ArrayList<unpersondata>()
    private val adapter = UnpersonRecycleadapter(UnpersonList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UnpersonrecordPageBinding.inflate(layoutInflater)
        recyclebinding=UnpersonrecordRecycleviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadUnpersonData()
        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupRecyclerView() {
        binding.unpersonList.apply {
            layoutManager = LinearLayoutManager(this@unpersonrecord)
            adapter = this@unpersonrecord.adapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@unpersonrecord, LinearLayout.VERTICAL))
        }
        adapter.itemClick = this@unpersonrecord
    }
    //졸음 기록 클릭시 졸음 맵으로 이동
    override fun onClick(view: View, id: Int, unpersonLa: Float,unpersonLo:Float,unpersontime: String) {
        val intent = Intent(this, unperson::class.java).apply {
            putExtra("ITEM_ID", id)
            putExtra("UNPERSON_TIME", unpersontime)
            putExtra("UNPERSONLA",unpersonLa)
            putExtra("UNPERSONLO",unpersonLo)
        }
        startActivity(intent)
    }
    //비인가 사용자 기록
    private fun loadUnpersonData() {
        val pref = getSharedPreferences("TokenPrefs",MODE_PRIVATE)
        var logintoken=pref.getString("token","Null")
        val requestData = putunpersonlist(jwt = logintoken?:"Null")
        unpersonlistApi.create().getunpersonlist(requestData).enqueue(object :
            Callback<List<unpersondata>> {
            override fun onResponse(call: Call<List<unpersondata>>, response: Response<List<unpersondata>>) {
                if (response.isSuccessful) {
                    UnpersonList.clear()
                    UnpersonList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    Log.d("unperosnrecord", "Data fetched successfully: ${response.body()}")
                } else {
                    Log.e("unpersonrecord", "Failed to fetch data: HTTP ${response.code()} ${response.message()}")
                    Toast.makeText(this@unpersonrecord, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<unpersondata>>, t: Throwable) {
                Toast.makeText(this@unpersonrecord, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}