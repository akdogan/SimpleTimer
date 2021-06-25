package com.akdogan.simpletimer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akdogan.simpletimer.data.domain.MListItem
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.databinding.AddButtonBinding
import com.akdogan.simpletimer.databinding.TimerItemBinding

class TimerListAdapter(
    private val addButtonFunction: () -> Unit,
    private val removeItemFunction: (Int) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataSet: List<MListItem> = emptyList()
    set(value){
        field = value
        notifyDataSetChanged()
    }

    class TimerListItem(val binding: TimerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: TimerObject, deleteFunction: () -> Unit) {
            setTimerType(item)
            this.binding.buttonIncrementTimer.setOnClickListener {
                item.incrementTime()
                this.updateLabel(item)
            }
            this.binding.buttonDecrementTimer.setOnClickListener {
                item.decrementTime()
                this.updateLabel(item)
            }
            this.binding.deleteButton.setOnClickListener {
                deleteFunction.invoke()
            }
            this.binding.toggleTimerType.setOnClickListener {
                item.toggleTimerType()
                setTimerType(item)
            }
        }

        private fun setTimerType(item: TimerObject){
            if (item.timerTypeAutomatic){
                binding.timerLabel.text = item.label
                binding.toggleTimerType.text = "Countdown"
                binding.buttonDecrementTimer.isEnabled = true
                binding.buttonIncrementTimer.isEnabled = true
            } else {
                binding.timerLabel.text = "∞"
                binding.toggleTimerType.text = "Manual"
                binding.buttonDecrementTimer.isEnabled = false
                binding.buttonIncrementTimer.isEnabled = false
            }
        }

        private fun updateLabel(item: TimerObject) {
            binding.timerLabel.text = item.label
        }
    }

    class TimerListAddButton(val binding: AddButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(clickFunction: () -> Unit){
            binding.buttonAddTimer.setOnClickListener{
                clickFunction.invoke()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is TimerListItem -> holder.onBind(dataSet[position] as TimerObject) {
                removeItemFunction.invoke(position)
            }
            is TimerListAddButton -> holder.onBind(addButtonFunction)
            else -> throw IllegalStateException("Something went severely wrong dude!")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> TimerListItem(
                TimerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> TimerListAddButton(
                AddButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == dataSet.lastIndex) 1 else 0
    override fun getItemCount(): Int = dataSet.size

}