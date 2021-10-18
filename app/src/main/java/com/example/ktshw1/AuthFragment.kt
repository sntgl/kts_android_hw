package com.example.ktshw1

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel

import androidx.navigation.fragment.findNavController
import com.example.ktshw1.auth.AuthViewModel


import android.content.Intent
import com.example.ktshw1.datastore.DatastoreViewModel
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import com.example.ktshw1.utils.toast


class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModel()
    private val datastoreViewModel: DatastoreViewModel by viewModel()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
        datastoreViewModel.passOnBoarding()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.loadingLiveData.value != true)
            viewModel.openLoginPage()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTH_REQUEST_CODE && data != null) {
            val tokenExchangeRequest = AuthorizationResponse.fromIntent(data)
                ?.createTokenExchangeRequest()
            val exception = AuthorizationException.fromIntent(data)
            when {
                tokenExchangeRequest != null && exception == null -> {
                    viewModel.onAuthCodeReceived(tokenExchangeRequest)

                } exception != null -> {
                    viewModel.onAuthCodeFailed(exception)
                    viewModel.openLoginPage()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindViewModel() {
        viewModel.openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.authSuccessLiveData.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }


    private fun openAuthPage(intent: Intent) {
        startActivityForResult(intent, AUTH_REQUEST_CODE)
    }

    companion object {
        private const val AUTH_REQUEST_CODE = 342
    }
}