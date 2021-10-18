package com.example.ktshw1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.databinding.FragmentOnBoardingBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlinx.coroutines.flow.collect



class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    private val binding: FragmentOnBoardingBinding by viewBinding(FragmentOnBoardingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onboardingViewpager.adapter = OnBoardingAdapter()
        binding.onboardingViewpagerIndicator.attachToPager(binding.onboardingViewpager)
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }
    }
}
