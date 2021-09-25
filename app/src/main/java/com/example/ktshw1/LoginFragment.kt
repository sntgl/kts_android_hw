package com.example.ktshw1

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var pass: TextInputEditText? = null
    private var email: TextInputEditText? = null
    private var emailLayout: TextInputLayout? = null
    private var passLayout: TextInputLayout? = null
    private var loginBtn: Button? = null
    private var passValid = false
    private var emailValid = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailLayout = view.findViewById(R.id.email_field_layout)
        passLayout = view.findViewById(R.id.password_field_layout)
        email = view.findViewById(R.id.email_field)
        pass = view.findViewById(R.id.password_field)
        loginBtn = view.findViewById(R.id.login_button)
        updateButton()
        bindFields()
    }

    private fun bindFields() {
        //Password field
        pass?.setOnEditorActionListener { _, actionId, _ ->
            checkPassError()
            //Автоматическое нажатие на кнопки при валидном вводе+нажатии Enter
            if (passValid && emailValid) {
                loginBtn?.performClick()
            }
            return@setOnEditorActionListener (passLayout?.isErrorEnabled ?: false)
        }
        pass?.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus)
                checkPassError()
        }
        //Email field
        email?.setOnEditorActionListener { _, _, _ ->
            checkEmailError()
            return@setOnEditorActionListener (emailLayout?.isErrorEnabled ?: false)
        }
        email?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                checkEmailError()
        }
        //Login field
        loginBtn?.setOnClickListener {
            updateButton()
            val checkPass: Boolean = checkPassError()
            if (!checkEmailError() && !checkPass)
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

    private fun checkPassError(): Boolean {
        val textLength: Int = pass?.text!!.length
        val err: Boolean = textLength < PASSWORD_MIN_LENGTH
        passLayout?.isErrorEnabled = err
        if (err)
            passLayout?.error = getString(R.string.min_length_warning)
        passValid = !err
        updateButton()
        return err
    }

    private fun checkEmailError(): Boolean {
        val text = email?.text.toString()
        val err: Boolean = !Patterns.EMAIL_ADDRESS.matcher(text).matches()
        emailLayout?.isErrorEnabled = err
        if (err)
            emailLayout?.error = getString(R.string.email_warning)
        emailValid = !err
        updateButton()
        return err
    }
    
    private fun updateButton() {
        //Нажимаемой оставил специально - чтобы можно было посмотреть подсказки
        if (emailValid && passValid)
            loginBtn?.alpha = 1.0F
        else
            loginBtn?.alpha = 0.5F
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pass = null
        email = null
        passLayout = null
        emailLayout = null
        loginBtn = null
    }

    companion object {
        private const val PASSWORD_MIN_LENGTH: Int = 8
    }
}