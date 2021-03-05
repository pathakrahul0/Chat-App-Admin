package com.adminapp.ui.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.adminapp.adapter.EmployeeGroupAdapter
import com.adminapp.databinding.ActivityGroupBinding
import com.adminapp.interfaces.OnItemClicks
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupActivity : AppCompatActivity() {

    @Inject
    lateinit var preference: Preference

    private lateinit var viewModel: GroupViewModel
    private lateinit var binding: ActivityGroupBinding
    private var employeeList = ArrayList<Employee>()
    private var employeeIdList = ArrayList<String>()
    private var employeeAdapter: EmployeeGroupAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        employeeIdList.add(preference.getUserId()!!)

        employeeAdapter = EmployeeGroupAdapter(employeeList, this, object : OnItemClicks {
            override fun onEmployeeClick(position: Int) {
                if (employeeList[position].isSelected) {
                    if (!employeeIdList.contains(employeeList[position].id)) {
                        employeeIdList.add(employeeList[position].id!!)
                    }
                } else {
                    if (employeeIdList.contains(employeeList[position].id))
                        employeeIdList.remove(employeeList[position].id)
                }
            }
        })
        binding.rvEmployee.adapter = employeeAdapter

        viewModel.getEmployees()

        viewModel.employeesData.observe({ lifecycle }) {
            employeeList.clear()
            employeeList.addAll(it)
            employeeAdapter?.notifyDataSetChanged()
        }

        binding.btnCreateGroup.setOnClickListener {
            if (!binding.etFullName.text.toString().isEmpty())
                viewModel.createGroup(
                    name = binding.etFullName.text.toString(),
                    employeeIdList
                )
            else Snackbar.make(binding.lGroup, "Please enter group name", Snackbar.LENGTH_LONG)
                .show()

        }

        viewModel.isUpdated.observe({ lifecycle }) {
            if (it) {
                onBackPressed()
                Snackbar.make(binding.lGroup, "Group created", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

}