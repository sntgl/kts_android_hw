package com.example.ktshw1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    val model: AppViewModel by viewModels()
    private val binding: FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindFields()
    }

    private fun bindFields() {
        // Обновление ViewModel при изменении полей pass/email
        binding.passwordField.doAfterTextChanged { model.onEditPass(it.toString()) }
        binding.emailField.doAfterTextChanged { model.onEditEmail(it.toString()) }
        // Изменение ошибок при изменении ViewModel

        model.loginEmailValid.observe(viewLifecycleOwner, { valid ->
            binding.emailFieldLayout.isErrorEnabled = !valid
            if (!valid)
                binding.emailFieldLayout.error = getString(R.string.login_email_warning)
        })
        model.loginPassValid.observe(viewLifecycleOwner, { valid ->
            binding.passwordFieldLayout.isErrorEnabled = !valid
            if (!valid)
                binding.passwordFieldLayout.error = getString(R.string.login_min_length_warning)
        })
        // Изменение прозрачности кнопки навигации при изменении ViewModel
        model.loginState.observe(viewLifecycleOwner, { valid ->
            if (valid)
                binding.loginButton.alpha = 1.0F
            else
                binding.loginButton.alpha = 0.5F
        })
        // Навигация с фрагмента
        binding.loginButton.setOnClickListener {
            if (model.loginState.value == true)
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
        // Автоматическое нажатие на кнопки при валидном вводе+нажатии Enter
        binding.passwordField.setOnEditorActionListener { _, _, _ ->
            if (model.loginState.value == true) {
                binding.loginButton.performClick()
            }
            return@setOnEditorActionListener (model.loginState.value != true)
        }
    }
}