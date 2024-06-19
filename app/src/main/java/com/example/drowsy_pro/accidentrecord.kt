package com.example.drowsy_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.drowsy_pro.databinding.AccidentrecordPageBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL


class accidentrecord : AppCompatActivity() {
    private lateinit var binding: AccidentrecordPageBinding
    private lateinit var lineChart: LineChart
    private lateinit var graphMenuAdapter: ArrayAdapter<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccidentrecordPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lineChart = findViewById(R.id.lineChart)
        setupGraphMenu()
        updateGraph()

        binding.okButton.setOnClickListener {
            updateGraph()
        }
        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupGraphMenu() {
        graphMenuAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.accident_menu,
            android.R.layout.simple_spinner_item
        )
        graphMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.graphMenu.adapter = graphMenuAdapter
    }
    //공공데이터 받아와 그래프 그리기
    private fun updateGraph() {
        val selectedMenuItem = binding.graphMenu.selectedItem.toString()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val jsonData = URL("사고통계URL").readText()
                val jsonObject = JSONObject(jsonData)
                val data = jsonObject.getJSONArray("data")

                val yearMap = HashMap<Int, Float>()

                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    val year = item.getInt("연도")
                    var value = 0f

                    when (selectedMenuItem) {
                        "부상자 수" -> value = item.getInt("부상_명").toFloat()
                        "사고건 수" -> value = item.getInt("사고_건").toFloat()
                        "사망자 수" -> value = item.getInt("사망_명").toFloat()
                    }

                    if (yearMap.containsKey(year)) {
                        val currentValue = yearMap[year] ?: 0f
                        yearMap[year] = currentValue + value
                    } else {
                        yearMap[year] = value
                    }
                }

                val entries = ArrayList<Entry>()
                for ((year, value) in yearMap) {
                    entries.add(Entry(year.toFloat(), value))
                }
                //그래프 그리기
                runOnUiThread {
                    val lineDataSet = LineDataSet(entries, selectedMenuItem)
                    lineDataSet.axisDependency = YAxis.AxisDependency.LEFT

                    val lineData = LineData(lineDataSet)
                    lineChart.data = lineData

                    val xAxis: XAxis = lineChart.xAxis
                    xAxis.granularity = 1f
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            return value.toInt().toString()
                        }
                    }
                    xAxis.position = XAxis.XAxisPosition.BOTTOM

                    val leftAxis: YAxis = lineChart.axisLeft
                    leftAxis.axisMinimum = 0f

                    val rightAxis: YAxis = lineChart.axisRight
                    rightAxis.isEnabled = false

                    lineChart.setTouchEnabled(true)
                    lineChart.setPinchZoom(true)
                    lineChart.description.isEnabled = false
                    lineChart.animateX(1000)
                    lineChart.invalidate()
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error fetching data: ${e.message}")
            }
        }
    }
}
