package com.akdogan.simpletimer.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.akdogan.simpletimer.Constants
import com.akdogan.simpletimer.Constants.BUNDLE_KEY_NUMBER_OF_SETS
import com.akdogan.simpletimer.Constants.BUNDLE_KEY_TIMER_LIST
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.ServiceLocator
import com.akdogan.simpletimer.data.domain.*
import com.akdogan.simpletimer.databinding.TimerFragmentBinding
import com.akdogan.simpletimer.service.TimerService

class TimerFragment : Fragment() {

    companion object {
        fun newInstance() = TimerFragment()
        const val TAG_CALLBACK = "CALLBACK_TRACING"
        const val TAG_LIFECYCLE_SERVICE = "LIFECYCLE_SERVICE"
    }

    // Service connection
    private var mService: TimerService? = null
    private var mBound = MutableLiveData<Boolean>(false)

    private val connection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.ServiceBinder
            mService = binder.getService()
            setupServiceObservers()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound.postValue(false)
        }
    }

    private var _binding: TimerFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val list = requireArguments()
            .getParcelableArrayList<TimerTransferObject>(BUNDLE_KEY_TIMER_LIST)
            ?.toDomain() ?: emptyList()

        val sets = requireArguments().getInt(BUNDLE_KEY_NUMBER_OF_SETS, 1)

        viewModel = ViewModelProvider(
            this,
            TimerViewModelFactory(sets, list, ServiceLocator.repo)
        ).get(TimerViewModel::class.java)

        _binding = TimerFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startMyService()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        bindToService()
        super.onStart()
    }

    override fun onStop() {
        unBindFromService()
        super.onStop()
    }

    private fun getTransferList(): ArrayList<TimerTransferObject>{
        return ArrayList(viewModel.getTimerList().toTransfer())
    }

    private fun unBindFromService() {
        requireActivity().unbindService(connection)
        mBound.postValue(false)
    }

    private fun bindToService() {
        val intent = Intent(requireContext(), TimerService::class.java)
        requireActivity().bindService(intent, connection, Context.BIND_ABOVE_CLIENT)
    }

    private fun startMyService() {
        val intent = Intent(requireContext(), TimerService::class.java)
            .putParcelableArrayListExtra(Constants.SERVICE_KEY_TIMER_LIST, getTransferList())
            .putExtra(Constants.SERVICE_KEY_NUMBER_OF_SETS, viewModel.numberOfSets)
        requireActivity().startService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupServiceObservers(){
        mService?.currentTime?.observe(viewLifecycleOwner){
            Log.i(TAG_LIFECYCLE_SERVICE, "Fragment received from Service: $it")
            binding.timerLabel.text = it.millisToSeconds().getTimeAsString()
        }

        mService?.countingUp?.observe(viewLifecycleOwner){
            if (it == true){
                binding.timerFragmentRootLayout.isClickable = true
                binding.timerFragmentRootLayout.setOnClickListener {
                    mService?.userPressedNext()
                }
            } else if (it == false){
                binding.timerFragmentRootLayout.isClickable = false
                binding.timerFragmentRootLayout.setOnClickListener(null)
            }
        }
        mService?.currentSet?.observe(viewLifecycleOwner){
            binding.timerSetsTitle.text = getString(R.string.timer_sets_title, it)
        }
        mService?.currentRound?.observe(viewLifecycleOwner){
            binding.timerRoundsTitle.text = getString(R.string.timer_rounds_title, it)
        }
        mService?.allTimersAreFinished?.observe(viewLifecycleOwner){
            Log.i(TAG_LIFECYCLE_SERVICE, "Fragment received final call: $it")
            if (it == true){
                binding.timerLabel.text = "YEAH!!"
            }
        }
    }

}