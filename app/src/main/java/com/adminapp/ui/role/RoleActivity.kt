package com.adminapp.ui.role

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.ActivityRoleBinding
import com.adminapp.ui.employee_login.EmployeeLoginActivity
import com.adminapp.ui.main.MainActivity

class RoleActivity : AppCompatActivity() {

    private lateinit var viewModel: RoleViewModel
    private lateinit var binding: ActivityRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RoleViewModel::class.java)
        binding = ActivityRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initView()


    }

    private fun initView() {
        binding.btnAdmin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnEmployee.setOnClickListener {
            startActivity(Intent(this, EmployeeLoginActivity::class.java))
        }
    }

}