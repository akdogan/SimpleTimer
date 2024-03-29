package com.akdogan.simpletimer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.akdogan.simpletimer.Constants.BUNDLE_KEY_NUMBER_OF_SETS
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.databinding.MainFragmentBinding
import com.akdogan.simpletimer.ui.BackPressConsumer
import com.akdogan.simpletimer.ui.printBackStack
import com.akdogan.simpletimer.ui.timer.TimerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(), BackPressConsumer {

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    // Not sure, but docs say this is the way:
    // https://developer.android.com/topic/libraries/view-binding
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var timerListAdapter: TimerListAdapter

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MainFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerListAdapter = TimerListAdapter(
            viewModel::onCreateNewItem,
            viewModel::onIncrementClicked,
            viewModel::onDecrementClicked,
            viewModel::onToggleType,
            viewModel::onDeleteItem
        )
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

        startObserving()
        printBackStack()
    }

    private fun startObserving() {
        // todo also use flow
        viewModel.numberOfSets.observe(viewLifecycleOwner) { numberOfSets: Int ->
            binding.setsLabel.text = getString(R.string.sets_label, numberOfSets)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timerList.collect {
                    timerListAdapter.submitList(it)
                }
            }
        }
    }

    private fun navigateToTimer() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, TimerFragment::class.java, getBundleWithData())
            .addToBackStack(null)
            .commit()
    }

    private fun getBundleWithData(): Bundle {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY_NUMBER_OF_SETS, viewModel.numberOfSets.value ?: 1)
        return bundle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return true
    }
}
