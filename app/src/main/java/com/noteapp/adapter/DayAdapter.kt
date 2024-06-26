package com.noteapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noteapp.databinding.DayItemBinding
import com.noteapp.models.Day

class DayAdapter(private val dayList: List<Day>) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(private val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val day = dayList[position]
            binding.apply {
                txtDay.text = day.day
                txtDayInMonth.text = day.date
                txtMonthDay.text = day.month
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayAdapter.DayViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = dayList.size
}
