package com.adminapp.ui.employee_list_activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.ActivityEmployeeListBinding
import com.adminapp.prefrences.Preference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeListActivity : AppCompatActivity() {

    val TAG = "EmployeeListActivity"

    @Inject
    lateinit var preference: Preference

    private lateinit var viewModel: EmployeeListActivityViewModel
    private lateinit var binding: ActivityEmployeeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmployeeListActivityViewModel::class.java)
        binding = ActivityEmployeeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, preference.getUserPhone()!!)
    }
}