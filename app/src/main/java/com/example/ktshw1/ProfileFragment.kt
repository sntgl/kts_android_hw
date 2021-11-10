package com.example.ktshw1

import Database
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentProfileBinding
import com.example.ktshw1.datastore.DatastoreViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class ProfileFragment() : Fragment(R.layout.fragment_profile) {
    private val datastoreViewModel: DatastoreViewModel by viewModel()

    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scope = CoroutineScope(Dispatchers.IO)

        binding.logoutButton.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setMessage("Выйти и очистить все данные?")
                .setNegativeButton("нет") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("да") { dialog, which ->
                    scope.launch {
                        Database.instance.clearAllTables()
                        datastoreViewModel.clear()
                    }
                }
                .show()
        }


        viewLifecycleOwner.lifecycleScope.launch {
            datastoreViewModel.onBoardingPassedFlow
                .filter { it == null }
                .collect {
                    datastoreViewModel.passOnBoarding()
                    findNavController().navigate(R.id.action_mainFragment_to_authFragment)
                }
        }


    }
}