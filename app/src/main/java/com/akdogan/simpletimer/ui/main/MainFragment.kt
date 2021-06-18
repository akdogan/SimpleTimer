package com.akdogan.simpletimer.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akdogan.simpletimer.Consts
import com.akdogan.simpletimer.Consts.BUNDLE_KEY_NUMBER_OF_SETS
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.data.domain.toTransfer
import com.akdogan.simpletimer.databinding.MainFragmentBinding
import com.akdogan.simpletimer.ui.timer.TimerFragment

class MainFragment : Fragment() {

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }

        const val LABEL_TRACING = "LABEL_TRACING"
    }

    // Not sure, but docs say this is the way:
    // https://developer.android.com/topic/libraries/view-binding
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var timerListAdapter: TimerListAdapter

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        _binding = MainFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timerListAdapter = TimerListAdapter(
            viewModel::addTimer,
            viewModel::removeTimer
        )
        binding.timerList.adapter = timerListAdapter

        binding.buttonIncrementSet.setOnClickListener {

            Log.i(LABEL_TRACING, "padding start ${it.paddingStart}, end ${it.paddingEnd}")
            viewModel.incrementSet()
        }

        binding.buttonDecrementSet.setOnClickListener {
            viewModel.decrementSet()
        }

        binding.startButton.setOnClickListener {
            navigateToTimer()
        }

        viewModel.numberOfSets.observe(viewLifecycleOwner){ numberOfSets: Int ->
            Log.i(LABEL_TRACING, "numSets observe valled with $numberOfSets")
            binding.setsLabel.text = getString(R.string.sets_label, numberOfSets)
        }

        viewModel.listOfTimers.observe(viewLifecycleOwner){
            it?.let{
                timerListAdapter.dataSet = it
            }
        }




        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToTimer(){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, TimerFragment::class.java, getBundleWithData() )
            .addToBackStack(null)
            .commit()
    }

    fun getBundleWithData(): Bundle{
        //val listOfTimerObjects = viewModel.getTimerList()
        val listOfTransferObjects = viewModel.getTimerList().toTransfer()
        val b = Bundle()
        val arrList = ArrayList(listOfTransferObjects)
        b.putParcelableArrayList(Consts.BUNDLE_KEY_TIMER_LIST, arrList)
        b.putInt(BUNDLE_KEY_NUMBER_OF_SETS, viewModel.numberOfSets.value ?: 1)
        return b
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}