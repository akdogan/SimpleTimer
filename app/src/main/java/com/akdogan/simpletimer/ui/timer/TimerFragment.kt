package com.akdogan.simpletimer.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.akdogan.simpletimer.Constants.BUNDLE_KEY_NUMBER_OF_SETS
import com.akdogan.simpletimer.Constants.BUNDLE_KEY_TIMER_LIST
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.ServiceLocator
import com.akdogan.simpletimer.data.domain.TimerTransferObject
import com.akdogan.simpletimer.data.domain.toDomain
import com.akdogan.simpletimer.databinding.TimerFragmentBinding
import com.akdogan.simpletimer.service.TimerService
import kotlinx.coroutines.launch

class TimerFragment : Fragment() {

    companion object {
        fun newInstance() = TimerFragment()
    }

    // Service connection
    private var mService: TimerService? = null
    private var mBound = false

    private val connection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.ServiceBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

    }

    private var _binding: TimerFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TimerViewModel

    private var mPlayer: MediaPlayer? = null

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

        mPlayer = MediaPlayer.create(requireContext(), R.raw.end_alarm)

        viewModel.timerLabel.observe(viewLifecycleOwner) { label: String? ->
            label?.let {
                binding.timerLabel.text = it
            }
        }

        viewModel.playSound.observe(viewLifecycleOwner) {
            if (it == true){
                playSound()
                viewModel.onPlaySoundDone()
            }
        }

        viewModel.currentSet.observe(viewLifecycleOwner){
            binding.timerSetsTitle.text = getString(R.string.timer_sets_title, it)
        }

        viewModel.currentRound.observe(viewLifecycleOwner){
            binding.timerRoundsTitle.text = getString(R.string.timer_rounds_title, it)
        }

        viewModel.countingUp.observe(viewLifecycleOwner){
            if (it == true){
                binding.timerFragmentRootLayout.isClickable = true
                binding.timerFragmentRootLayout.setOnClickListener {
                    viewModel.userStoppedTimer()
                }
            } else if (it == false){
                binding.timerFragmentRootLayout.isClickable = false
                binding.timerFragmentRootLayout.setOnClickListener(null)
            }
        }

        viewModel.startNextSet()
        // TODO Should only start if service is not already running i guess
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

    private fun unBindFromService() {
        requireActivity().unbindService(connection)
        mBound = false
    }

    private fun bindToService() {
        val intent = Intent(requireContext(), TimerService::class.java)
        requireActivity().bindService(intent, connection, Context.BIND_ABOVE_CLIENT)
    }

    private fun playSound() {
        lifecycleScope.launch {
            mPlayer?.start()
        }
    }

    private fun startMyService() {
        val intent = Intent(requireContext(), TimerService::class.java)
        requireActivity().startService(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mPlayer?.release()
        mPlayer = null
    }




}