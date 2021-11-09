package com.example.ktshw1

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentProfileBinding
import timber.log.Timber

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        NavigationUI.setupActionBarWithNavController(binding.bottomNavigation, )
//        binding.bottomNavigation.selectedItemId = R.id.bottomNavigationProfile
//        binding.bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.bottomNavigationFeed -> {
//                    Timber.d("Feed")
//                    findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
//                }
//                R.id.bottomNavigationProfile -> {
//                    Timber.d("Profile")
//                }
//                else -> return@setOnItemSelectedListener false
//            }
//            return@setOnItemSelectedListener true
//        }
    }
}