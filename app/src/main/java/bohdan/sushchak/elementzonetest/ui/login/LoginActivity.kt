package bohdan.sushchak.elementzonetest.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import bohdan.sushchak.elementzonetest.R
import bohdan.sushchak.elementzonetest.ui.MainActivity
import bohdan.sushchak.elementzonetest.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*

import org.kodein.di.generic.instance

class LoginActivity : BaseActivity(){

    private val viewModelFactory: LoginViewModelFactory by instance()
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LoginViewModel::class.java)

        btnLogIn.setOnClickListener { logIn() }
        bindUI()
    }

    private fun bindUI() = launch{
        viewModel.apiKey.observe(this@LoginActivity, Observer { data ->
            if(data != null) {
                startMainActivity()
            }
        })

        viewModel.logInError.observe(this@LoginActivity, Observer { err ->
            Toast.makeText(this@LoginActivity, err.message, Toast.LENGTH_LONG).show()
        })
    }

    private fun logIn() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if(email.isBlank() || password.isBlank()){
            var emailErr: String? = null

            if(email.isBlank())
                emailErr = getString(R.string.err_email_is_empty)
            if(!isEmailValid(email))
                emailErr = getString(R.string.err_email_is_not_valid)

            etPasswordLayout.error = if(password.isBlank()) getString(R.string.err_password_is_empty) else null
            etEmailLayout.error = emailErr
            return
        }

        viewModel.logIn(email = email, password = password)
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isEmailValid(email: String) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
