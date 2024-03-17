package com.example.drowsy_pro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drowsy_pro.databinding.DrivingrecordRecyclerviewBinding

class DrivingRecycleadapter(val Drivinglist:ArrayList<Drivingrecycledata>) : RecyclerView.Adapter<DrivingRecycleadapter.Holder>() {
    interface ItemClick {
        fun onClick(view : View, position : Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrivingRecycleadapter.Holder {
        val binding = DrivingrecordRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: DrivingRecycleadapter.Holder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
        holder.start_location.text = Drivinglist[position].start_location
        holder.arrival_location.text = Drivinglist[position].arrival_location
        holder.start_time.text = Drivinglist[position].start_time
        holder.arrival_time.text = Drivinglist[position].arrival_time
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
}