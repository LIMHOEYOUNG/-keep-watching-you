package com.example.drowsy_pro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drowsy_pro.databinding.DrivingrecordRecyclerviewBinding
import com.example.drowsy_pro.operationrecord.NaverGeocodeResponse
import com.example.drowsy_pro.operationrecord.drivingdata
import com.example.drowsy_pro.operationrecord.gcRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DrivingRecycleadapter(val Drivinglist:ArrayList<drivingdata>) : RecyclerView.Adapter<DrivingRecycleadapter.Holder>() {
    interface ItemClick {
        fun onClick(view : View, id : Int,startTime: String, endTime: String)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrivingRecycleadapter.Holder {
        val binding = DrivingrecordRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: DrivingRecycleadapter.Holder, position: Int) {
        val item = Drivinglist[position]
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,item.id,formatDateTime(item.start_time),formatDateTime(item.end_time))
        }
        val startcoords = "${item.start_location_longi},${item.start_location_lati}"
        val endcoords = "${item.end_location_longi},${item.end_location_lati}"

        //좌표를 주소로 변환
        gcRetrofitClient.createNavergcService().getReverseGeocoding(
            clientId = "w1ktctpg1u",
            clientSecret = "Vtli4mD8nVO99MfN8WzUlnSjm2YQBoZN8P1nbTzD",
            coords = startcoords
        ).enqueue(object : Callback<NaverGeocodeResponse> {
            override fun onResponse(call: Call<NaverGeocodeResponse>, response: Response<NaverGeocodeResponse>) {
                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    // 성공적으로 응답을 받았을 때 처리 로직
                    val address = response.body()?.results?.get(0)?.region?.let {
                        "${it.area1.name} ${it.area2.name} ${it.area3.name}"
                    }
                    holder.start_location.text ="출발 장소- $address" ?: "주소를 찾을 수 없음"
                } else {
                    // 요청에 실패했을 때의 처리 로직
                    holder.start_location.text = "주소 변환 실패"
                }
            }
            override fun onFailure(call: Call<NaverGeocodeResponse>, t: Throwable) {
                // API 호출 자체가 실패했을 때의 처리 로직
                holder.start_location.text = "API 호출 실패: ${t.message}"
            }
        })
        //좌표를 주소로 변환
        gcRetrofitClient.createNavergcService().getReverseGeocoding(
            clientId = "w1ktctpg1u",
            clientSecret = "Vtli4mD8nVO99MfN8WzUlnSjm2YQBoZN8P1nbTzD",
            coords = endcoords
        ).enqueue(object : Callback<NaverGeocodeResponse> {
            override fun onResponse(call: Call<NaverGeocodeResponse>, response: Response<NaverGeocodeResponse>) {
                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    val address = response.body()?.results?.get(0)?.region?.let {
                        "${it.area1.name} ${it.area2.name} ${it.area3.name}"
                    }
                    holder.arrival_location.text = "도착 장소- $address"?: "주소를 찾을 수 없음"
                } else {
                    holder.arrival_location.text = "주소 변환 실패"
                }
            }
            override fun onFailure(call: Call<NaverGeocodeResponse>, t: Throwable) {
                holder.arrival_location.text = "API 호출 실패: ${t.message}"
            }
        })
        holder.start_time.text ="출발 시간- ${formatDateTime(item.start_time)}"
        holder.arrival_time.text ="도착 시간- ${formatDateTime(item.end_time)}"

    }

    override fun getItemCount(): Int {
        return Drivinglist.size
    }
    inner class Holder(val binding:DrivingrecordRecyclerviewBinding ) : RecyclerView.ViewHolder(binding.root) {
        val start_location = binding.startLocation
        val arrival_location = binding.arrivalLocation
        val start_time=binding.startTime
        val arrival_time=binding.arrivalTime
    }
    //시간을 보기 편하게 변환
    fun formatDateTime(dateTime: String): String {
        // 밀리초 포함 포맷
        val inputFormatWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormatWithMillis.timeZone = TimeZone.getTimeZone("UTC")

        // 밀리초 미포함 포맷
        val inputFormatWithoutMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormatWithoutMillis.timeZone = TimeZone.getTimeZone("UTC")

        val inputFormatWithT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        inputFormatWithoutMillis.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        // 날짜 파싱 시도 (밀리초 포함)
        try {
            val date = inputFormatWithMillis.parse(dateTime)
            return outputFormat.format(date)
        } catch (e: ParseException) {
            try {
                val date = inputFormatWithoutMillis.parse(dateTime)
                return outputFormat.format(date)
            } catch (e: ParseException) {
                try{
                    val date = inputFormatWithT.parse(dateTime)
                    return outputFormat.format(date)
                }
                catch(e: ParseException){
                    e.printStackTrace()
                    return dateTime
                }
            }
        }
    }
}