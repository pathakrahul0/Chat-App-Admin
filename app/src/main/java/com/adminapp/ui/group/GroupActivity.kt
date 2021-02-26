package com.adminapp.ui.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adminapp.adapter.EmployeeGroupAdapter
import com.adminapp.databinding.ActivityGroupBinding
import com.adminapp.interfaces.OnItemClicks
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
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
            viewModel.createGroup(
                name = "Sample Group",
                employeeIdList
            )
        }


    }
}