package com.example.ktshw1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var passValid = false
    private var emailValid = false
    val model: AppViewModel by viewModels()
    private val binding: FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindFields()
    }

    private fun bindFields() {
        binding.passwordField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                model.onEditPass(s.toString())
            }
        })
        binding.emailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                model.onEditEmail(s.toString())
            }
        })
        model.loginEmailValid.observe(viewLifecycleOwner, { valid ->
            emailLayout?.isErrorEnabled = !valid
            if (!valid)
                emailLayout?.error = getString(R.string.login_email_warning)
        })
        model.loginPassValid.observe(viewLifecycleOwner, { valid ->
            passLayout?.isErrorEnabled = !valid
            if (!valid)
                passLayout?.error = getString(R.string.login_min_length_warning)
        })
        model.loginState.observe(viewLifecycleOwner, { valid ->
            if (valid)
                loginBtn?.alpha = 1.0F
            else
                loginBtn?.alpha = 0.5F
        })
        binding.loginButton.setOnClickListener {
            if (model.loginState.value == true)
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
        //Автоматическое нажатие на кнопки при валидном вводе+нажатии Enter
        binding.passwordField.setOnEditorActionListener { _, _, _ ->
            if (model.loginState.value == true) {
                loginBtn?.performClick()
            }
            return@setOnEditorActionListener (model.loginState.value != true)
        }
    }
}