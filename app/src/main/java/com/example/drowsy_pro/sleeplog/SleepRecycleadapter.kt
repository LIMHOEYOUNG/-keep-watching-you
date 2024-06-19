package com.example.drowsy_pro.sleeplog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drowsy_pro.databinding.SleeprrecordRecycleviewBinding
import com.example.drowsy_pro.operationrecord.NaverGeocodeResponse
import com.example.drowsy_pro.operationrecord.gcRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class SleepRecycleadapter(val Sleeplist:ArrayList<sleeplogdata>) : RecyclerView.Adapter<SleepRecycleadapter.Holder>() {
    interface ItemClick {
        fun onClick(view : View, id : Int, sleepLa: Float, sleepLo: Float, sleeptime:String)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepRecycleadapter.Holder {
        val binding = SleeprrecordRecycleviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    inner class Holder(val binding:SleeprrecordRecycleviewBinding ) : RecyclerView.ViewHolder(binding.root) {
        val sleep_location = binding.sleepLocation
        val sleep_time = binding.sleepTime

    }
    override fun onBindViewHolder(holder: SleepRecycleadapter.Holder, position: Int) {
        val item = Sleeplist[position]
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,item.id,item.sleep_location_longi,item.sleep_location_lati,formatDateTime(item.sleep_time))
        }
        val sleepcoords = "${item.sleep_location_longi},${item.sleep_location_lati}"

        //좌표를 주소로 변환
        gcRetrofitClient.createNavergcService().getReverseGeocoding(
            clientId = "w1ktctpg1u",
            clientSecret = "Vtli4mD8nVO99MfN8WzUlnSjm2YQBoZN8P1nbTzD",
            coords = sleepcoords
        ).enqueue(object : Callback<NaverGeocodeResponse> {
            override fun onResponse(call: Call<NaverGeocodeResponse>, response: Response<NaverGeocodeResponse>) {
                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    // 성공적으로 응답을 받았을 때 처리 로직
                    val address = response.body()?.results?.get(0)?.region?.let {
                        "${it.area1.name} ${it.area2.name} ${it.area3.name}"
                    }
                    holder.sleep_location.text ="졸음 장소- $address" ?: "주소를 찾을 수 없음"
                } else {
                    // 요청에 실패했을 때의 처리 로직
                    holder.sleep_location.text = "주소 변환 실패"
                }
            }
            override fun onFailure(call: Call<NaverGeocodeResponse>, t: Throwable) {
                // API 호출 자체가 실패했을 때의 처리 로직
                holder.sleep_location.text = "API 호출 실패: ${t.message}"
            }
        })
        holder.sleep_time.text ="출발 시간- ${formatDateTime(item.sleep_time)}"

    }

    override fun getItemCount(): Int {
        return Sleeplist.size
    }
    fun formatDateTime(dateTime: String): String {
        // 밀리초 포함 포맷
        val inputFormatWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormatWithMillis.timeZone = TimeZone.getTimeZone("UTC")

        // 밀리초 미포함 포맷
        val inputFormatWithoutMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
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
                e.printStackTrace()
                return dateTime
            }
        }
    }
}