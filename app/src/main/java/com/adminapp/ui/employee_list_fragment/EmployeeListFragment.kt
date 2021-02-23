package com.adminapp.ui.employee_list_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.adminapp.R
import com.adminapp.adapter.EmployeeAdapter
import com.adminapp.databinding.EmployeeListFragmentBinding
import com.adminapp.interfaces.OnItemClicks
import com.adminapp.model.Employee

class EmployeeListFragment : Fragment() {


    private lateinit var viewModel: EmployeeListViewModel
    private lateinit var binding: EmployeeListFragmentBinding
    private var employeeList = ArrayList<Employee>()
    private var employeeAdapter: EmployeeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EmployeeListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmployeeListViewModel::class.java)
        viewModel.getEmployees()
        binding.fab.setOnClickListener { findNavController().navigate(R.id.navigation_employee_details) }

        employeeAdapter = EmployeeAdapter(employeeList, requireActivity(), object : OnItemClicks {
            override fun onEmployeeClick(position: Int) {
                val bundle = Bundle()
                bundle.putParcelable("employeeData", employeeList[position])
                findNavController().navigate(R.id.navigation_employee_details, bundle)
            }


        })
        binding.rvEmployee.adapter = employeeAdapter

        viewModel.employeesData.observe({ lifecycle }) {
            employeeList.clear()
            employeeList.addAll(it)
            employeeAdapter?.notifyDataSetChanged()
        }

    }

}