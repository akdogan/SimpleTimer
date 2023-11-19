package com.akdogan.simpletimer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akdogan.simpletimer.data.domain.AddButton
import com.akdogan.simpletimer.data.domain.MListItem
import com.akdogan.simpletimer.data.domain.TimerDomain
import com.akdogan.simpletimer.databinding.AddButtonBinding
import com.akdogan.simpletimer.databinding.TimerItemBinding

class TimerListAdapter(
    private val addButtonFunction: () -> Unit,
    private val onIncrement: (TimerDomain) -> Unit,
    private val onDecrement: (TimerDomain) -> Unit,
    private val onToggleType: (TimerDomain) -> Unit,
    private val removeItemFunction: (TimerDomain) -> Unit,
) : ListAdapter<MListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<MListItem>() {
        override fun areItemsTheSame(oldItem: MListItem, newItem: MListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MListItem, newItem: MListItem): Boolean {
            return oldItem == newItem
        }
    }

    class TimerViewHolder(
        private val binding: TimerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            item: TimerDomain,
            onIncrement: (TimerDomain) -> Unit,
            onDecrement: (TimerDomain) -> Unit,
            onToggleType: (TimerDomain) -> Unit,
            deleteFunction: () -> Unit,
        ) {
            setTimerType(item)
            this.binding.buttonIncrementTimer.setOnClickListener {
                onIncrement(item)
            }
            this.binding.buttonDecrementTimer.setOnClickListener {
                onDecrement(item)
            }
            this.binding.deleteButton.setOnClickListener {
                deleteFunction.invoke()
            }
            this.binding.toggleTimerType.setOnClickListener {
                onToggleType(item)
            }
        }

        private fun setTimerType(item: TimerDomain) {
            if (!item.manual) {
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
    }

    class AddButtonViewHolder(
        private val binding: AddButtonBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(clickFunction: () -> Unit) {
            binding.buttonAddTimer.setOnClickListener {
                clickFunction.invoke()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimerViewHolder -> {
                val item = getItem(position) as TimerDomain
                holder.onBind(
                    item,
                    onIncrement,
                    onDecrement,
                    onToggleType,
                ) { removeItemFunction(item) }
            }

            is AddButtonViewHolder -> holder.onBind(addButtonFunction)
            else -> throw IllegalStateException("Something went severely wrong dude!")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> TimerViewHolder(
                TimerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            1 -> AddButtonViewHolder(
                AddButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> error("Unknown viewtype <$viewType> received, expected 0 or 1")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is TimerDomain -> 0
            is AddButton -> 1
            else -> error("Uknown item type found at $position: ${item::class.java.simpleName}")
        }
    }

}