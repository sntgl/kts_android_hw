package com.example.ktshw1

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ktshw1.datastore.DatastoreViewModel
import com.example.ktshw1.model.UserInfo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class StartFragment : Fragment(R.layout.fragment_start) {
    private val datastoreViewModel: DatastoreViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
                datastoreViewModel.onBoardingPassedFlow.zip(datastoreViewModel.getApiKeyFlow)
                {onBoardingPassed, apiKey -> onBoardingPassed to apiKey}
                .onEach { (onBoardingPassed, apiKey) ->
                    Timber.d("zip. $onBoardingPassed, $apiKey")
                }
                .collect { (onBoardingPassed, apiKey) ->
                    if (apiKey != null && apiKey != "") {
                        UserInfo.authToken = apiKey //TODO походу дохнет(
                        findNavController().navigate(R.id.action_startFragment_to_mainFragment)
                    } else if (onBoardingPassed == true) {
                        findNavController().navigate(R.id.action_startFragment_to_authFragment)
                    } else {
                        findNavController().navigate(R.id.action_startFragment_to_onBoardingFragment)
                    }

                }
        }
    }
}