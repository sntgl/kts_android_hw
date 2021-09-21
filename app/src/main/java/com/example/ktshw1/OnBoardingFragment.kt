package com.example.ktshw1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    private val binding: FragmentOnBoardingBinding by viewBinding(FragmentOnBoardingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }
    }
}
