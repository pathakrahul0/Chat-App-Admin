package com.adminapp.ui.employee_login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.ActivityEmployeeLoginBinding
import com.adminapp.ui.admin_login.AdminLoginActivity
import com.adminapp.ui.employee_list_activity.EmployeeListActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeLoginActivity : AppCompatActivity() {

    private lateinit var viewModel: EmployeeLoginViewModel
    private lateinit var binding: ActivityEmployeeLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmployeeLoginViewModel::class.java)
        binding = ActivityEmployeeLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding.btnSendOtp.setOnClickListener {

            if (viewModel.validatePhone(binding.etPhone.text.toString())) {
                viewModel.getUserRef(
                    binding.etPhone.text.toString(),
                    this@EmployeeLoginActivity
                )
            } else
                Snackbar.make(
                    binding.lEmployeeLogin,
                    "Enter valid phone number",
                    Snackbar.LENGTH_LONG
                ).show()
        }

        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.isHideSendOtpBtns.value = false
            }
        })

        binding.btnVerifyOtp.setOnClickListener {
            if (viewModel.validateOTP(binding.etOtp.text.toString())) {
                viewModel.verifyPhoneNumberWithCode(binding.etOtp.text.toString())
            } else
                Snackbar.make(
                    binding.lEmployeeLogin,
                    "Enter valid OTP",
                    Snackbar.LENGTH_LONG
                ).show()


        }

        binding.tvResendOtp.setOnClickListener {
            if (viewModel.validatePhone(binding.etPhone.text.toString())) {
                viewModel.resendVerificationCode(
                    binding.etPhone.text.toString(),
                    this@EmployeeLoginActivity
                )
            } else
                Snackbar.make(
                    binding.lEmployeeLogin,
                    "Enter valid phone number",
                    Snackbar.LENGTH_LONG
                ).show()
        }

        binding.tvAdmin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        viewModel.isVerifiedUser.observe({ lifecycle }) {
            if (it) {
                startActivity(Intent(this, EmployeeListActivity::class.java))
                finishAffinity()
            } else
                Snackbar.make(binding.lEmployeeLogin, "Invalid User", Snackbar.LENGTH_LONG)
                    .show()
        }

        viewModel.isLoading.observe({ lifecycle }) {
            if (it) binding.loader.visibility = View.VISIBLE
            else binding.loader.visibility = View.GONE
        }

        viewModel.isHideSendOtpBtn.observe({ lifecycle }) {
            if (it) {
                binding.btnSendOtp.visibility = View.GONE
                binding.tilOtp.visibility = View.VISIBLE
                binding.btnVerifyOtp.visibility = View.VISIBLE
                binding.tvResendOtp.visibility = View.VISIBLE
            }else {
                binding.btnSendOtp.visibility = View.VISIBLE
                binding.tilOtp.visibility = View.GONE
                binding.btnVerifyOtp.visibility = View.GONE
                binding.tvResendOtp.visibility = View.GONE
            }
        }


    }
}