package com.akdogan.simpletimer.ui.main

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.akdogan.simpletimer.Constants
import com.akdogan.simpletimer.Constants.BUNDLE_KEY_NUMBER_OF_SETS
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.ServiceLocator
import com.akdogan.simpletimer.data.domain.toTransfer
import com.akdogan.simpletimer.databinding.MainFragmentBinding
import com.akdogan.simpletimer.ui.timer.TimerFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(ServiceLocator.repo)
        ).get(MainViewModel::class.java)

        _binding = MainFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timerListAdapter = TimerListAdapter(
            viewModel::addTimer,
            viewModel::removeTimer,
        ){ requestCode: Int ->
            startSoundPickerActivity(requestCode)
        }
        binding.timerList.adapter = timerListAdapter

        binding.buttonIncrementSet.setOnClickListener {
            viewModel.incrementSet()
        }

        binding.buttonDecrementSet.setOnClickListener {
            viewModel.decrementSet()
        }

        binding.startButton.setOnClickListener {
            navigateToTimer()
        }

        viewModel.numberOfSets.observe(viewLifecycleOwner) { numberOfSets: Int ->
            binding.setsLabel.text = getString(R.string.sets_label, numberOfSets)
        }

        viewModel.listOfTimers.observe(viewLifecycleOwner) {
            it?.let {
                timerListAdapter.dataSet = it
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun startSoundPickerActivity(requestCode: Int) {
        val intent = Intent("android.intent.action.RINGTONE_PICKER")
        startActivityForResult(intent, requestCode)
    }

    private fun navigateToTimer() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, TimerFragment::class.java, getBundleWithData())
            .addToBackStack(null)
            .commit()
    }

    fun getBundleWithData(): Bundle {
        val listOfTransferObjects = viewModel.getTimerList().toTransfer()
        val b = Bundle()
        val arrList = ArrayList(listOfTransferObjects)
        b.putParcelableArrayList(Constants.BUNDLE_KEY_TIMER_LIST, arrList)
        b.putInt(BUNDLE_KEY_NUMBER_OF_SETS, viewModel.numberOfSets.value ?: 1)
        return b
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.setSoundChoiceForItem(requestCode, uri)
        } else {
            Log.i("SoundManager", "activity result no macht")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun playSound(uri: Uri?) {
        Log.i("SoundManager", "received uri, $uri")
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            val ringtoneManager = RingtoneManager.getRingtone(requireContext(), uri)
            ringtoneManager.play()
            delay(1000)
            ringtoneManager.stop()
        }
    }


}