package com.adminapp.ui.employee_login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.ActivityEmployeeLoginBinding
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


        binding.btnSendOtp.setOnClickListener {
            viewModel.getUserRef(
                "${binding.etPhone.text}",
                this@EmployeeLoginActivity
            )
            binding.btnSendOtp.visibility = View.GONE
            binding.etOtp.visibility = View.VISIBLE
            binding.btnVerifyOtp.visibility = View.VISIBLE
            binding.tvResendOtp.visibility = View.VISIBLE
        }
        binding.btnVerifyOtp.setOnClickListener {
            viewModel.verifyPhoneNumberWithCode(
                "${binding.etOtp.text}",
            )
        }
        binding.tvResendOtp.setOnClickListener {
            viewModel.resendVerificationCode(
                "${binding.etPhone.text}",
                this@EmployeeLoginActivity
            )
        }

        viewModel.isVerifedUser.observe({ lifecycle }) {
            if (it)
                startActivity(Intent(this, EmployeeListActivity::class.java))
            else
                Snackbar.make(binding.lLogin, "Invalid User", Snackbar.LENGTH_LONG)

        }

    }


}