package com.anyjob.ui.login

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.anyjob.databinding.ActivityLoginBinding

import com.anyjob.R
import com.anyjob.extensions.afterTextChanged

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginViewModelFactory = LoginViewModelFactory();
        loginViewModel = ViewModelProvider(this@LoginActivity, loginViewModelFactory)[LoginViewModel::class.java]

        loginViewModel.apply {
            loginFormState.observe(this@LoginActivity, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                binding.getConfirmationCodeButton.isEnabled = loginState.isDataValid

                if (loginState.phoneNumberError != null) {
                    binding.phoneNumberField.error = getString(loginState.phoneNumberError)
                }
            })

            loginResult.observe(this@LoginActivity, Observer {
                val loginResult = it ?: return@Observer

                binding.loadingBar.visibility = View.GONE

                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                else if (loginResult.success != null) {
                    afterLogin(loginResult.success)
                }

                setResult(Activity.RESULT_OK)
                finish()
            })
        }

        binding.phoneNumberField.apply {
            afterTextChanged {
                loginViewModel.validateLoginForm(
                    binding.phoneNumberField.text.toString(),
                )
            }

            /*setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            binding.phoneNumberField.text.toString()
                        )
                }
                false
            }*/
        }

        binding.getConfirmationCodeButton.setOnClickListener {
            binding.loadingBar.visibility = View.VISIBLE
            loginViewModel.login(
                binding.phoneNumberField.text.toString()
            )
        }
    }

    private fun afterLogin(model: LoggedInUserView) {
        val displayName = model.displayName
        val welcomeMessage = "${getString(R.string.welcome)} $displayName"

        Toast.makeText(applicationContext, welcomeMessage, Toast.LENGTH_LONG)
             .show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT)
             .show()
    }
}