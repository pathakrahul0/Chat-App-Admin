package com.adminapp.ui.admin_login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.ActivityAdminLoginBinding
import com.adminapp.prefrences.Preference
import com.adminapp.ui.main.MainActivity
import com.adminapp.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminLoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminLoginViewModel
    private lateinit var binding: ActivityAdminLoginBinding

    @Inject
    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminLoginViewModel::class.java)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (validateAdmin())
            viewModel.adminLogin(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
            )
        }

        viewModel.adminLogin.observe({ lifecycle }) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
                preference.setIsAdmin(true)
                finishAffinity()
            }
            else
                Snackbar.make(binding.lAdminLogin, "Invalid User", Snackbar.LENGTH_LONG).show()
        }


        viewModel.isLoading.observe({ lifecycle }) {
            if (it) binding.loader.visibility = View.VISIBLE
            else binding.loader.visibility = View.GONE
        }

    }
    private fun validateAdmin(): Boolean {
        if (binding.etEmail.text.toString().isEmpty()) {
            Snackbar.make(binding.lAdminLogin, "Enter Email", Snackbar.LENGTH_LONG).show()
            return false
        }
        else if (!Constants.isValidMailId(binding.etEmail.text.toString())) {
            Snackbar.make(binding.lAdminLogin, "Invalid Email", Snackbar.LENGTH_LONG).show()
            return false
        }
        else if (binding.etPassword.text.toString().isEmpty()) {
            Snackbar.make(binding.lAdminLogin, "Enter Password", Snackbar.LENGTH_LONG).show()
            return false
        }
        else if (binding.etPassword.text.toString().length < 8) {
            Snackbar.make(binding.lAdminLogin, "Passwords must contain at least 8 characters", Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

}