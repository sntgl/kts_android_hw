package com.example.ktshw1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn = requireView().findViewById<Button>(R.id.start_button)
        val model: AppViewModel by viewModels()
        if (model.isOnBoardingPassed())
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        btn.setOnClickListener {
            model.onBoardingPass()
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }

    }
}
